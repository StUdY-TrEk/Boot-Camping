package com.sparta.studytrek.domain.reply.service;

import com.sparta.studytrek.common.ResponseText;
import com.sparta.studytrek.domain.auth.entity.User;
import com.sparta.studytrek.domain.comment.entity.ReviewComment;
import com.sparta.studytrek.domain.comment.repository.ReviewCommentRepository;
import com.sparta.studytrek.domain.reply.dto.ReplyRequestDto;
import com.sparta.studytrek.domain.reply.dto.ReplyResponseDto;
import com.sparta.studytrek.domain.reply.entity.ReviewReply;
import com.sparta.studytrek.domain.reply.repository.ReviewReplyRepository;
import com.sparta.studytrek.domain.review.repository.ReviewRepository;
import com.sparta.studytrek.notification.service.NotificationService;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewReplyService {

    private final ReviewReplyRepository replyRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final NotificationService notificationService;

    /**
     * 리뷰 댓글의 대댓글 작성
     *
     * @param reviewId   리뷰 ID
     * @param commentId  리뷰의 댓글 ID
     * @param requestDto 대댓글 요청 내용
     * @param user       유저 정보
     * @return 리뷰 댓글의 대댓글 응답 데이터
     */
    public ReplyResponseDto createReviewReply(Long reviewId, Long commentId,
        ReplyRequestDto requestDto, User user) throws IOException {
        reviewRepository.findByReviewId(reviewId);

        ReviewComment comment = reviewCommentRepository.findByReviewCommentId(commentId);
        ReviewReply reply = new ReviewReply(comment, user, requestDto);
        ReviewReply saveReply = replyRepository.save(reply);

        notificationService.createAndSendNotification(
            comment.getUser().getUsername(),
            ResponseText.NOTIFICATION_REVIEW_REPLY_CREATED.getMsg()
        );

        return new ReplyResponseDto(saveReply);
    }

    /**
     * 리뷰 댓글의 대댓글 수정
     *
     * @param reviewId   리뷰 ID
     * @param commentId  리뷰의 댓글 ID
     * @param replyId    리뷰의 대댓글 ID
     * @param requestDto 대댓글 요청 내용
     * @param user       유저 정보
     * @return 리뷰 댓글의 대댓글 응답 데이터
     */
    @Transactional
    public ReplyResponseDto updateReviewReply(Long reviewId, Long commentId, Long replyId,
        ReplyRequestDto requestDto, User user) {
        reviewRepository.findByReviewId(reviewId);
        reviewCommentRepository.findByReviewCommentId(commentId);

        ReviewReply reply = replyRepository.findByReviewReplyId(replyId);
        reply.updateReply(requestDto.getContent());
        return new ReplyResponseDto(reply);
    }

    /**
     * 리뷰 댓글의 대댓글 삭제
     *
     * @param reviewId  리뷰 ID
     * @param commentId 리뷰의 댓글 ID
     * @param replyId   리뷰의 대댓글 ID
     * @param user      유저 정보
     */
    public void deleteReviewReply(Long reviewId, Long commentId, Long replyId, User user) {
        reviewRepository.findByReviewId(reviewId);
        reviewCommentRepository.findByReviewCommentId(commentId);

        ReviewReply reply = replyRepository.findByReviewReplyId(replyId);
        replyRepository.delete(reply);
    }

    /**
     * 리뷰 댓글의 대댓글 전체 조회
     *
     * @param commentId  리뷰의 댓글 ID
     * @return 리뷰 댓글의 대댓글 목록
     */
    public List<ReplyResponseDto> getAllReviewReply(Long commentId) {
        List<ReviewReply> reviewReplies = replyRepository.findByReviewCommentIdOrderByCreatedAtDesc(commentId);
        return reviewReplies.stream().map(ReplyResponseDto::new).toList();
    }

    /**
     * 리뷰 댓글의 대댓글 단건 조회
     *
     * @param commentId 댓글 ID
     * @param replyId 대댓글 ID
     * @return 리뷰 댓글의 대댓글 정보
     */
    public ReplyResponseDto getReviewReply(Long commentId, Long replyId) {
        ReviewReply reviewReply = replyRepository.findByReviewCommentIdAndReplyId(commentId, replyId);
        return new ReplyResponseDto(reviewReply);
    }

}
