package com.sparta.studytrek.domain.answer.service;

import com.sparta.studytrek.common.ResponseText;
import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.domain.answer.dto.AnswerRequestDto;
import com.sparta.studytrek.domain.answer.dto.AnswerResponseDto;
import com.sparta.studytrek.domain.answer.entity.Answer;
import com.sparta.studytrek.domain.answer.repository.AnswerRepository;
import com.sparta.studytrek.domain.auth.entity.User;
import com.sparta.studytrek.domain.question.entity.Question;
import com.sparta.studytrek.domain.question.repository.QuestionRepository;
import com.sparta.studytrek.notification.service.NotificationService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final NotificationService notificationService;

    /**
     * 답변 작성
     *
     * @param questionId    질문 ID
     * @param requestDto    답변 작성 요청 데이터
     * @param user          요청한 유저의 정보
     * @return  답변 응답 데이터
     */
    public AnswerResponseDto createAnswer(Long questionId, AnswerRequestDto requestDto, User user) throws IOException {

        Question question = questionRepository.findByQuestionId(questionId);
        Answer answer = new Answer(requestDto, question, user);
        Answer savedAnswer = answerRepository.save(answer);

        notificationService.createAndSendNotification(
            question.getUser().getUsername(),
            ResponseText.NOTIFICATION_ANSWER_CREATED.getMsg()
        );

        return new AnswerResponseDto(savedAnswer);
    }

    /**
     * 답변 수정
     *
     * @param questionId    질문 ID
     * @param answerId      답변 ID
     * @param requestDto    답변 수정 요청 데이터
     * @param user  요청한 유저의 정보
     * @return  답변 응답 데이터
     */
    @Transactional
    public AnswerResponseDto updateAnswer(Long questionId, Long answerId, AnswerRequestDto requestDto, User user) {
        Question question = questionRepository.findByQuestionId(questionId);
        Answer answer = answerRepository.findByAnswerId(answerId);
        if (!answer.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ANSWER_UPDATE_NOT_AUTHORIZED);
        }
        answer.update(requestDto);
        return new AnswerResponseDto(answer);
    }

    /**
     * 답변 삭제
     *
     * @param questionId    질문 ID
     * @param answerId      답변 ID
     * @param user  요청한 유저의 정보
     */
    public void deleteAnswer(Long questionId, Long answerId, User user) {
        Question question = questionRepository.findByQuestionId(questionId);
        Answer answer = answerRepository.findByAnswerId(answerId);
        if (!answer.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ANSWER_DELETE_NOT_AUTHORIZED);
        }
        answerRepository.delete(answer);
    }

    /**
     * 답변 전체 조회
     *
     * @param questionId  페이지 정보
     * @return  답변 전체 목록
     */
    public List<AnswerResponseDto> getAnswers(Long questionId) {
        List<Answer> answerPage = answerRepository.findByQuestionIdOrderByCreatedAtDesc(questionId);
        return answerPage.stream().map(AnswerResponseDto::new).toList();
    }

    /**
     * 답변 단건 조회
     *
     * @param questionId    질문 ID
     * @param answerId      답변 ID
     * @return  해당 답변의 응답 데이터
     */
    public AnswerResponseDto getAnswer(Long questionId, Long answerId) {
        Answer answer = answerRepository.findByQuestionIdAndAnswerId(questionId, answerId);
        return new AnswerResponseDto(answer);
    }

    public List<String> listUserAnswers(User user) {
        List<Answer> answers = answerRepository.findAllByUserOrderByCreatedAtDesc(user);
        return answers.stream()
            .map(Answer::getContent)
            .collect(Collectors.toList());
    }

    public int countUserAnswers(User user) {
        return answerRepository.countByUser(user);
    }
}
