package com.sparta.studytrek.domain.study.service;

import java.util.List;
import java.util.stream.Collectors;

import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.domain.auth.entity.User;
import com.sparta.studytrek.domain.study.dto.StudyRequestDto;
import com.sparta.studytrek.domain.study.dto.StudyResponseDto;
import com.sparta.studytrek.domain.study.entity.Study;
import com.sparta.studytrek.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;

    /**
     * 스터디 모집글 생성
     *
     * @param request 스터디 모집글 생성 데이터
     * @param user    인증된 유저 정보
     * @return 생성된 스터디 모집글 응답 데이터
     */
    @Transactional
    public StudyResponseDto createStudy(StudyRequestDto request, User user) {
        Study study = new Study(
            user,
            request.getTitle(),
            request.getCategory(),
            request.getContent(),
            request.getMaxCount(),
            request.getPeriodExpected(),
            request.getCycle()
        );
        Study savedStudy = studyRepository.save(study);
        return buildStudyResponseDto(savedStudy);
    }

    /**
     * 스터디 모집글 전체 조회 (페이징 처리)
     *
     * @param pageable 페이지 정보
     * @return 스터디 모집글 페이징 조회 데이터
     */
    @Transactional(readOnly = true)
    public Page<StudyResponseDto> getAllStudies(Pageable pageable) {
        Page<Study> studyPage = studyRepository.findAllByOrderByCreatedAtDesc(pageable);
        return studyPage.map(this::buildStudyResponseDto);
    }

    /**
     * 스터디 모집글 단건 조회
     *
     * @param id 조회할 스터디 모집글 ID
     * @return 스터디 모집글 응답 데이터
     */
    @Transactional(readOnly = true)
    public StudyResponseDto getStudy(Long id) {
        Study study = studyRepository.findByStudyId(id);
        return buildStudyResponseDto(study);
    }

    /**
     * 스터디 모집글 수정
     *
     * @param id      수정할 스터디 모집글 ID
     * @param request 스터디 모집글 수정 데이터
     * @param user    인증된 유저 정보
     * @return 수정된 스터디 모집글 응답 데이터
     */
    @Transactional
    public StudyResponseDto updateStudy(Long id, StudyRequestDto request, User user) {
        Study study = studyRepository.findByStudyId(id);

        if (!study.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.STUDY_UPDATE_NOT_AUTHORIZED);
        }

        study.updateStudy(
            request.getTitle(),
            request.getCategory(),
            request.getContent(),
            request.getMaxCount(),
            request.getPeriodExpected(),
            request.getCycle());

        return buildStudyResponseDto(study);
    }

    /**
     * 스터디 모집글 삭제
     *
     * @param id   삭제할 스터디 모집글 ID
     * @param user 인증된 유저 정보
     */
    @Transactional
    public void deleteStudy(Long id, User user) {
        Study study = studyRepository.findByStudyId(id);

        if (!study.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.STUDY_DELETE_NOT_AUTHORIZED);
        }

        studyRepository.delete(study);
    }

    /**
     * 스터디 모집글 응답 데이터 빌더
     *
     * @param study 스터디 모집글 엔티티
     * @return 스터디 모집글 응답 데이터
     */
    private StudyResponseDto buildStudyResponseDto(Study study) {
        return StudyResponseDto.builder()
            .id(study.getId())
            .title(study.getTitle())
            .content(study.getContent())
            .category(study.getCategory())
            .maxCount(study.getMaxCount())
            .periodExpected(study.getPeriodExpected())
            .cycle(study.getCycle())
            .createdAt(study.getCreatedAt().toString())
            .modifiedAt(study.getModifiedAt().toString())
            .build();
    }

    @Transactional(readOnly = true)
    public int countUserStudies(User user) {
        return studyRepository.countByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public List<String> listUserStudies(User user) {
        List<Study> studies = studyRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        return studies.stream()
            .map(Study::getTitle)
            .collect(Collectors.toList());
    }
}
