package com.sparta.studytrek.domain.answer.repository;

import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.domain.answer.entity.Answer;
import com.sparta.studytrek.domain.auth.entity.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long>{

    List<Answer> findByQuestionIdOrderByCreatedAtDesc(Long questionId);

    Optional<Answer> findByQuestionIdAndId(Long questionId, Long answerId);

    default Answer findByAnswerId(Long answerId){
        return findById(answerId).orElseThrow(() -> new CustomException(ErrorCode.NOTFOUND_ANSWER));
    }

    default Answer findByQuestionIdAndAnswerId(Long questionId, Long answerId){
        return findByQuestionIdAndId(questionId, answerId).orElseThrow(() -> new CustomException(ErrorCode.NOTFOUND_ANSWER));
    }

    List<Answer> findAllByUserOrderByCreatedAtDesc(User user);

    int countByUser(User user);
}
