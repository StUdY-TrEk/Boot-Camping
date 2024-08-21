package com.sparta.studytrek.domain.comment.service;

import com.sparta.studytrek.common.ResponseText;
import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.domain.answer.entity.Answer;
import com.sparta.studytrek.domain.answer.repository.AnswerRepository;
import com.sparta.studytrek.domain.auth.entity.User;
import com.sparta.studytrek.domain.comment.dto.AnswerCommentRequestDto;
import com.sparta.studytrek.domain.comment.dto.AnswerCommentResponseDto;
import com.sparta.studytrek.domain.comment.entity.AnswerComment;
import com.sparta.studytrek.domain.comment.repository.AnswerCommentRepository;
import com.sparta.studytrek.domain.question.entity.Question;
import com.sparta.studytrek.domain.question.repository.QuestionRepository;
import com.sparta.studytrek.notification.service.NotificationService;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerCommentService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AnswerCommentRepository answerCommentRepository;
    private final NotificationService notificationService;

    /**
     * 질문에 대한 답변의 댓글 작성
     *
     * @param questionId    질문 ID
     * @param answerId      답변 ID
     * @param requestDto    댓글 요청 내용
     * @param user          유저 정보
     * @return  질문에 대한 답변의 댓글 응답 데이터
     */
    public AnswerCommentResponseDto createAnswerComment(Long questionId, Long answerId,
        AnswerCommentRequestDto requestDto, User user) throws IOException {
        Question question = questionRepository.findByQuestionId(questionId);
        Answer answer = answerRepository.findByAnswerId(answerId);

        AnswerComment answerComment = new AnswerComment(answer, user, requestDto);
        AnswerComment saveComment = answerCommentRepository.save(answerComment);

        notificationService.createAndSendNotification(
            answer.getUser().getUsername(),
            ResponseText.NOTIFICATION_ANSWER_COMMENT_CREATED.getMsg()
        );

        return new AnswerCommentResponseDto(saveComment);
    }

    /**
     * 질문에 대한 답변의 댓글 수정
     *
     * @param questionId    질문 ID
     * @param answerId      답변 ID
     * @param commentId     댓글 ID
     * @param requestDto    댓글 수정 요청 데이터
     * @param user  인증된 유저의 정보
     * @return  댓글 응답 데이터
     */
    public AnswerCommentResponseDto updateAnswerComment(Long questionId, Long answerId, Long commentId, AnswerCommentRequestDto requestDto, User user) {
        Question question = questionRepository.findByQuestionId(questionId);
        Answer answer = answerRepository.findByAnswerId(answerId);
        AnswerComment answerComment = answerCommentRepository.findByAnswerCommentId(commentId);
        if (!answerComment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.COMMENT_UPDATE_NOT_AUTHORIZED);
        }
        answerComment.update(requestDto);
        return new AnswerCommentResponseDto(answerComment);
    }

    /**
     * 댓글 삭제
     *
     * @param questionId    질문 ID
     * @param answerId      답변 ID
     * @param commentId     댓글 ID
     * @param user  요청한 유저의 정보
     */
    public void deleteAnswerComment(Long questionId, Long answerId, Long commentId, User user) {
        Question question = questionRepository.findByQuestionId(questionId);
        Answer answer = answerRepository.findByAnswerId(answerId);
        AnswerComment answerComment = answerCommentRepository.findByAnswerCommentId(commentId);
        if (!answerComment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.COMMENT_DELETE_NOT_AUTHORIZED);
        }
        answerCommentRepository.delete(answerComment);
    }

    /**
     * 댓글 전체 조회
     *
     * @param answerId 답변 ID
     * @return  댓글의 전체 목록
     */
    public List<AnswerCommentResponseDto> getAnswerComments(Long answerId) {
        List<AnswerComment> answerComments = answerCommentRepository.findByAnswerIdOrderByCreatedAtDesc(answerId);
        return answerComments.stream().map(AnswerCommentResponseDto::new).toList();
    }

    /**
     * 댓글 단건 조회
     *
     * @param answerId  답변 ID
     * @param commentId 댓글 ID
     * @return  댓글 단건 조회 목록
     */
    public AnswerCommentResponseDto getAnswerComment(Long answerId, Long commentId) {
        AnswerComment answerComment = answerCommentRepository.findByAnswerIdAndCommentId(answerId, commentId);
        return new AnswerCommentResponseDto(answerComment);
    }

}
