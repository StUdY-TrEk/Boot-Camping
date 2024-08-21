package com.sparta.studytrek.domain.study.repository;

import java.util.List;

import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// JpaRepository와 StudyRepositoryCustom을 함께 상속받아 기본 CRUD 작업과 커스텀 쿼리를 모두 지원
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryCustom {

    default Study findByStudyId(Long studyId){
        return findById(studyId).orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
    }

    int countByUserId(Long userId);

    List<Study> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
