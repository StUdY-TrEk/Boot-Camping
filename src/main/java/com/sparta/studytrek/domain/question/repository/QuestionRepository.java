package com.sparta.studytrek.domain.question.repository;

import java.util.List;

import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.domain.auth.entity.User;
import com.sparta.studytrek.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
    default Question findByQuestionId(Long questionId){
        return findById(questionId).orElseThrow(() -> new CustomException(ErrorCode.NOTFOUND_QUESTION));
    }

    int countByUser(User user);

    List<Question> findAllByUserOrderByCreatedAtDesc(User user);
}