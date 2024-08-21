package com.sparta.studytrek.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "실패했습니다."),

    // Token
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
    TOKEN_EXPIRATION(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 재로그인 해주세요."),
    NOT_SUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    FALSE_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰입니다."),
    HEADER_NOT_FOUND_AUTH(HttpStatus.BAD_REQUEST, "권한 헤더가 잘못되었거나 누락되었습니다."),
    TOKEN_VALIDATE(HttpStatus.BAD_REQUEST, "Invalid JWT token."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    UNMATCHED_TOKEN(HttpStatus.BAD_REQUEST, "일치하지 않는 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "등록되지 않은 토큰입니다."),

    // User
    INVALID_USERNAME(HttpStatus.BAD_REQUEST,"아이디는 최소 6자 이상, 20자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 합니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"최소 8자 이상, 15자 이하이며 알파벳 대소문자(az, AZ), 숫자(0~9),특수문자로 구성되어야 합니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "등록되지 않은 사용자입니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
    INCORRECT_USER(HttpStatus.BAD_REQUEST,"사용자가 동일하지 않습니다."),
    DUPLICATE_USER(HttpStatus.BAD_REQUEST,"중복된 사용자가 존재합니다."),
    WITHDRAW_USER(HttpStatus.BAD_REQUEST, "탈퇴한 회원입니다."),
    BANNED_USER(HttpStatus.FORBIDDEN, "BAN 처리된 사용자입니다."),
    BAD_MANAGER_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 암호입니다."),
    CAMP_NAME_REQUIRED_FOR_BOOTCAMP_ROLE(HttpStatus.BAD_REQUEST, "캠프 이름은 부트캠프 권한자만 사용 가능합니다."),
    HEADER_NOT_FOUND_REFRESH(HttpStatus.BAD_REQUEST,"헤더에 토큰이 존재하지 않습니다."),
    USER_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다. 로그인해주세요."),
    STATUS_NOT_FOUND(HttpStatus.BAD_REQUEST, "상태를 찾을 수 없습니다."),
    NOT_AUTHENTICATED_LOGIN(HttpStatus.FORBIDDEN, "권한에 맞지 않는 로그인입니다."),

    // AOP
    NOT_AUTHENTICATED_USER(HttpStatus.FORBIDDEN, "부트캠프 관리자만 접근 가능합니다."),
    NOT_AUTHENTICATED_ADMIN(HttpStatus.FORBIDDEN, "접근 불가능한 권한입니다."),
    NOT_AUTHENTICATED_BOOTCAMP(HttpStatus.FORBIDDEN, "수강생만 작성이 가능합니다."),

    // Review
    NOTFOUND_REVIEW(HttpStatus.BAD_REQUEST, "해당 리뷰는 존재하지 않습니다."),
    REVIEW_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "해당 리뷰를 작성한 사용자가 아닙니다."),

    // Camp
    NOTFOUND_CAMP(HttpStatus.BAD_REQUEST, "캠프를 찾을 수 없습니다."),
    NOTFOUND_CAMP_USER(HttpStatus.BAD_REQUEST, "해당 캠프에 참가한 이력이 존재하지 않습니다."),
    DUPLICATE_CAMP_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 캠프 이름입니다."),

    // Role
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "역할을 찾을 수 없습니다."),

    //Question
    NOTFOUND_QUESTION(HttpStatus.BAD_REQUEST, "해당 질문은 존재하지 않습니다."),
    QUESTION_UPDATE_NOT_AUTHORIZED(HttpStatus.BAD_REQUEST, "해당 질문을 수정할 권한이 없습니다."),
    QUESTION_DELETE_NOT_AUTHORIZED(HttpStatus.BAD_REQUEST, "해당 질문을 삭제할 권한이 없습니다."),

    //Answer
    NOTFOUND_ANSWER(HttpStatus.BAD_REQUEST, "해당 답변은 존재하지 않습니다."),
    ANSWER_UPDATE_NOT_AUTHORIZED(HttpStatus.BAD_REQUEST, "해당 답변을 수정할 권한이 없습니다."),
    ANSWER_DELETE_NOT_AUTHORIZED(HttpStatus.BAD_REQUEST, "해당 답변을 삭제할 권한이 없습니다."),


    // recruitment
    NOTFOUND_RECRUITMENT(HttpStatus.BAD_REQUEST,"해당 모집글은 존재하지 않습니다."),
    RECRUITMENT_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "해당 모집글을 작성한 사용자가 아닙니다."),

    // Comment
    NOTFOUND_REVIEW_COMMENT(HttpStatus.BAD_REQUEST, "해당 댓글은 존재하지 않습니다."),

    // Reply
    NOTFOUND_REVIEW_REPLY(HttpStatus.BAD_REQUEST, "해당 대댓글은 존재하지 않습니다."),

    // Profile
    NOTFOUND_PROFILE(HttpStatus.BAD_REQUEST, "해당 프로필을 찾을 수 없습니다."),
    PROFILE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "프로필을 수정할 권한이 없습니다."),
    PROFILE_STATUS_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "프로필 상태가 허용되지 않습니다."),

    // Study
    STUDY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 스터디 모집글은 존재하지 않습니다."),
    STUDY_UPDATE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "해당 스터디 모집글을 수정할 권한이 없습니다."),
    STUDY_DELETE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "해당 스터디 모집글을 삭제할 권한이 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),
    COMMENT_UPDATE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "댓글 수정 권한이 없습니다."),
    COMMENT_DELETE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "댓글 삭제 권한이 없습니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "대댓글이 존재하지 않습니다."),
    REPLY_UPDATE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "대댓글 수정 권한이 없습니다."),
    REPLY_DELETE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "대댓글 삭제 권한이 없습니다."),

    // Like
    ALREADY_LIKE(HttpStatus.BAD_REQUEST,"이미 좋아요를 눌렀습니다."),
    NOTFOUND_LIKE(HttpStatus.BAD_REQUEST,"좋아요를 누르지 않았습니다."),
    //Answer_Comment
    NOTFOUND_ANSWER_COMMENT(HttpStatus.BAD_REQUEST, "해당 댓글은 존재하지 않습니다."),
    // Message
    MESSAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "메세지를 찾을 수 없습니다."),
    // Notification
    NOTIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST,"알림을 찾을 수 없습니다."),
    // S3
    FILE_SIZE_EXCEED(HttpStatus.BAD_REQUEST, "이미지 크기가 초과되었습니다."),
    FILE_TYPE_REQUIRED(HttpStatus.BAD_REQUEST, "이미지 파일이 필요합니다."),

    // Summary
    SUMMARY_GENERATION_FAILED(HttpStatus.BAD_REQUEST, "요약을 실패했습니다");

    private HttpStatus httpStatus;
    private String msg;
}
