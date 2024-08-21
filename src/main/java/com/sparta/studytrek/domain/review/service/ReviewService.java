package com.sparta.studytrek.domain.review.service;

import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.domain.auth.entity.User;
import com.sparta.studytrek.domain.auth.service.UserService;
import com.sparta.studytrek.domain.camp.entity.Camp;
import com.sparta.studytrek.domain.camp.service.CampService;
import com.sparta.studytrek.domain.review.dto.ReviewRequestDto;
import com.sparta.studytrek.domain.review.dto.ReviewResponseDto;
import com.sparta.studytrek.domain.review.entity.Review;
import com.sparta.studytrek.domain.review.repository.ReviewRepository;
import com.sparta.studytrek.summary.service.SummaryService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CampService campService;
    private final UserService userService;
    private final SummaryService summaryService;

    /**
     * 리뷰 작성
     *
     * @param requestDto 리뷰 등록 요청 데이터
     * @param user       요청한 유저의 정보
     * @return 리뷰 응답 데이터
     */
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto requestDto, User user) {

        List<String> userCampNames = userService.getUserCampNames(user.getId());

        boolean isUserInCamp = userCampNames.stream()
            .anyMatch(campName -> campName.equalsIgnoreCase(requestDto.getCampName()));

        if (!isUserInCamp) {
            throw new CustomException(ErrorCode.NOTFOUND_CAMP_USER);
        }

        Camp camp = campService.findByName(requestDto.getCampName());

        Review review = new Review(requestDto, user, camp);
        Review creatReview = reviewRepository.save(review);

        long reviewCount = reviewRepository.countByCampId(camp.getId());
        if (reviewCount % 10 == 0) {
            updateCampSummary(camp.getId());
        }

        return new ReviewResponseDto(creatReview);
    }

    /**
     * 리뷰 수정
     *
     * @param id         리뷰 ID
     * @param requestDto 리뷰 수정 요청 데이터
     * @param user       요청한 유저의 정보
     * @return 리뷰 응답 데이터
     */
    @Transactional
    public ReviewResponseDto updateReview(Long id, ReviewRequestDto requestDto, User user) {
        Review review = reviewRepository.findByReviewId(id);
        reqUserCheck(review.getUser().getId(), user.getId());

        List<String> userCampNames = userService.getUserCampNames(user.getId());

        boolean isUserInCamp = userCampNames.stream()
            .anyMatch(campName -> campName.equalsIgnoreCase(requestDto.getCampName()));

        if (!isUserInCamp) {
            throw new CustomException(ErrorCode.NOTFOUND_CAMP_USER);
        }

        Camp camp = campService.findByName(requestDto.getCampName());

        review.updateReview(requestDto, camp);

        return new ReviewResponseDto(review);
    }

    /**
     * 리뷰 삭제
     *
     * @param id   리뷰 ID
     * @param user 요청한 유저의 정보
     */
    public void deleteReview(Long id, User user) {
        Review review = reviewRepository.findByReviewId(id);
        reqUserCheck(review.getUser().getId(), user.getId());

        reviewRepository.delete(review);
    }

    /**
     * 리뷰 전체 조회
     *
     * @param pageable 페이지 정보
     * @return 리뷰 전체 목록
     */
    public Page<ReviewResponseDto> getAllReviews(Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findAllByOrderByCreatedAtDesc(pageable);
        return reviewPage.map(ReviewResponseDto::new);
    }

    /**
     * 리뷰 단건 조회
     *
     * @param id 리뷰 ID
     * @return 해당 리뷰의 응답 데이터
     */
    public ReviewResponseDto getReview(Long id) {
        Review review = reviewRepository.findByReviewId(id);
        return new ReviewResponseDto(review);
    }

    /**
     * 리뷰를 작성한 유저와 해당 기능을 요청한 유저가 동일한지
     *
     * @param reviewUserId 리뷰를 작성한 유저 ID
     * @param userId       유저 ID
     */
    public void reqUserCheck(Long reviewUserId, Long userId) {
        if (!reviewUserId.equals(userId)) {
            throw new CustomException(ErrorCode.REVIEW_NOT_AUTHORIZED);
        }
    }

    public String updateCampSummary(Long campId) {
        List<Review> reviews = reviewRepository.findTop10ByCampIdOrderByCreatedAtDesc(campId);

        List<String> contents = reviews.stream()
            .map(Review::getContent)
            .collect(Collectors.toList());

        return summaryService.summarizeText(contents, campId).orElse("");
    }

    public int countUserReviews(User user) {
        return reviewRepository.countByUserId(user.getId());
    }

    public List<String> listUserReviews(User user) {
        List<Review> reviews = reviewRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        return reviews.stream()
            .map(Review::getTitle)
            .collect(Collectors.toList());
    }
}
