package com.sparta.studytrek.domain.like.service;

import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.domain.auth.entity.User;
import com.sparta.studytrek.domain.like.entity.StudyLike;
import com.sparta.studytrek.domain.like.repository.StudyLikeRepository;
import com.sparta.studytrek.domain.study.entity.Study;
import com.sparta.studytrek.domain.study.repository.StudyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyLikeService {

    private final StudyLikeRepository studyLikeRepository;
    private final StudyRepository studyRepository;

    /**
     * 좋아요 추가
     *
     * @param studyId   스터디 ID
     * @param user  유저 정보
     * @return  좋아요 응답 데이터
     */
    @Transactional
    public int studyLike(Long studyId, User user) {
        Study study = studyRepository.findByStudyId(studyId);

        Optional<StudyLike> existingLike = studyLikeRepository.findByStudyIdAndUserId(studyId, user.getId());

        if (existingLike.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_LIKE);
        }

        StudyLike studyLike = new StudyLike(study, user);
        studyLikeRepository.save(studyLike);

        return studyLikeRepository.countLikeByStudyId(studyId);
    }

    /**
     * 좋아요 취소
     *
     * @param studyId   스터디 ID
     * @param user  유저 정보
     * @return  좋아요 취소 응답 데이터
     */
    @Transactional
    public int studyUnlike(Long studyId, User user) {
        Study study = studyRepository.findByStudyId(studyId);

        StudyLike studyLike = studyLikeRepository.findByStudyIdAndUserId(studyId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOTFOUND_LIKE));

        studyLikeRepository.delete(studyLike);
        return studyLikeRepository.countLikeByStudyId(studyId);
    }

    @Transactional(readOnly = true)
    public List<String> getLikedStudies(User user) {
        List<StudyLike> likes = studyLikeRepository.findByUser(user);
        return likes.stream()
            .map(studyLike -> studyLike.getStudy().getTitle())
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int getLikedStudyCount(User user) {
        return studyLikeRepository.countByUser(user);
    }
}
