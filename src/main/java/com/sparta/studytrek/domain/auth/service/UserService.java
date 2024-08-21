package com.sparta.studytrek.domain.auth.service;

import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.domain.auth.dto.LoginRequestDto;
import com.sparta.studytrek.domain.auth.dto.SignUpRequestDto;
import com.sparta.studytrek.domain.auth.dto.SignUpResponseDto;
import com.sparta.studytrek.domain.auth.dto.TokenResponseDto;
import com.sparta.studytrek.domain.auth.entity.Role;
import com.sparta.studytrek.domain.auth.entity.Status;
import com.sparta.studytrek.domain.auth.entity.User;
import com.sparta.studytrek.domain.auth.entity.UserRoleEnum;
import com.sparta.studytrek.domain.auth.entity.UserStatusEnum;
import com.sparta.studytrek.domain.auth.entity.UserType;
import com.sparta.studytrek.domain.auth.repository.CampUserRepository;
import com.sparta.studytrek.domain.auth.repository.RoleRepository;
import com.sparta.studytrek.domain.auth.repository.StatusRepository;
import com.sparta.studytrek.domain.auth.repository.UserRepository;
import com.sparta.studytrek.domain.camp.entity.Camp;
import com.sparta.studytrek.domain.camp.service.CampService;
import com.sparta.studytrek.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CampUserRepository campUserRepository;
    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final CampService campService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입 로직
     *
     * @param requestDto 회원가입 요청 데이터
     * @return 유저 회원가입 정보 반환
     */
    @Transactional
    public SignUpResponseDto signup(SignUpRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }

        // ROLE 확인
        UserRoleEnum roleEnum = UserRoleEnum.valueOf(requestDto.getUserRole());
        Role role = roleRepository.findByRole(roleEnum)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));

        // 사용자 저장
        User user = new User(username, password, requestDto.getName(), requestDto.getUserAddr(), UserType.NORMAL, role);
        userRepository.save(user);

        if (roleEnum == UserRoleEnum.USER) {
            // USER 일 경우 userStatus 저장
            UserStatusEnum userStatus = UserStatusEnum.getDefault();

            Status findStatus = statusRepository.findByStatus(userStatus)
                    .orElseThrow(() -> new CustomException(ErrorCode.STATUS_NOT_FOUND));

            user.addStatus(findStatus);
        } else if (roleEnum == UserRoleEnum.BOOTCAMP) {
            // BOOTCAMP 일 경우 campUser 저장
            String campName = requestDto.getCampName();
            if (campName == null || campName.isEmpty()) {
                throw new CustomException(ErrorCode.CAMP_NAME_REQUIRED_FOR_BOOTCAMP_ROLE);
            }

            Camp camp = campService.findByName(campName);
            user.addCamp(camp);
        }

        return SignUpResponseDto.builder().user(user).build();
    }

    /**
     * 로그인 로직
     *
     * @param requestDto 로그인 요청 정보
     * @return
     */
    public TokenResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.isWithdrawn()) {
            throw new CustomException(ErrorCode.WITHDRAW_USER);
        } else if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        UserRoleEnum roleEnum = UserRoleEnum.valueOf(requestDto.getRole());
        if (!user.getRole().getRole().equals(roleEnum)) {
            throw new CustomException(ErrorCode.NOT_AUTHENTICATED_LOGIN);
        }

        String accessToken = jwtUtil.createAccessToken(user.getUsername(),
            user.getRole().toString());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername(),
            user.getRole().toString());

        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        return new TokenResponseDto(accessToken, refreshToken);
    }


    /**
     * 로그아웃 로직
     *
     * @param user 로그아웃할 사용자
     */
    public void logout(User user) {
        refreshTokenService.removeRefreshToken(user.getId());
    }


    /**
     * 회원탈퇴 로직
     *
     * @param user 탈퇴할 사용자
     */
    public void withdraw(User user) {
        refreshTokenService.removeRefreshToken(user.getId());

        user.withdraw();
        userRepository.save(user);
    }

    /**
     * 유저 정보 가져오기
     *
     * @param username 사용자 ID
     * @return user class 타입 반환 / 유저 정보
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public Status findByStatus(UserStatusEnum status) {
        return statusRepository.findByStatus(status)
            .orElseThrow(() -> new CustomException(ErrorCode.STATUS_NOT_FOUND));
    }

    public Role findRoleByName(UserRoleEnum roleEnum) {
        return roleRepository.findByRole(roleEnum).orElseThrow(
            () -> new CustomException(ErrorCode.ROLE_NOT_FOUND)
        );
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * 해당 유저가 참여한 캠프 목록 조회
     *
     * @param userId 유저 ID
     * @return 참여한 캠프 목록
     */
    @Transactional
    public List<String> getUserCampNames(Long userId) {
        return campUserRepository.findCampNamesById(userId);
    }
}