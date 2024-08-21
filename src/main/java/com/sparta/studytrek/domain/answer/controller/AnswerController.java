package com.sparta.studytrek.domain.answer.controller;

import com.sparta.studytrek.common.ApiResponse;
import com.sparta.studytrek.common.ResponseText;
import com.sparta.studytrek.domain.answer.dto.AnswerRequestDto;
import com.sparta.studytrek.domain.answer.dto.AnswerResponseDto;
import com.sparta.studytrek.domain.answer.service.AnswerService;
import com.sparta.studytrek.security.UserDetailsImpl;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions/{questionId}/answers")
public class AnswerController {

    private final AnswerService answerService;

    /**
     * 답변 작성 API
     *
     * @param questionId    질문 ID
     * @param requestDto    답변 작성 데이터
     * @param userDetails   인증된 유저 정보
     * @return  답변 작성 응답 데이터
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createAnswer(@PathVariable Long questionId,
        @RequestBody AnswerRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        AnswerResponseDto responseDto = answerService.createAnswer(questionId, requestDto,
            userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.ANSWER_CREATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.CREATED.value()))
            .data(responseDto)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 답변 수정 APi
     *
     * @param questionId    질문 ID
     * @param answerId      답변 ID
     * @param requestDto    답변 수정 데이터
     * @param userDetails   인증된 유저 정보
     * @return  답변 수정 응답 데이터
     */
    @Transactional
    @PutMapping("/{answerId}")
    public ResponseEntity<ApiResponse> updateAnswer(@PathVariable Long questionId,
        @PathVariable("answerId") Long answerId,
        @RequestBody AnswerRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        AnswerResponseDto responseDto = answerService.updateAnswer(questionId, answerId,requestDto, userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.ANSWER_UPDATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 답변 삭제 API
     *
     * @param questionId    질문 ID
     * @param answerId      답변 ID
     * @param userDetails   인증된 유저 정보
     * @return  답변 삭제 응답 데이터
     */
    @DeleteMapping("/{answerId}")
    public ResponseEntity<ApiResponse> deleteAnswer(@PathVariable Long questionId,
        @PathVariable("answerId") Long answerId,
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        answerService.deleteAnswer(questionId, answerId, userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.ANSWER_DELETE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.NO_CONTENT.value()))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 답변 전체 조회 API
     *
     * @param questionId  질문 ID
     * @return  답변 전체 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllAnswers(@PathVariable Long questionId)
    {
        List<AnswerResponseDto> answers = answerService.getAnswers(questionId);
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.ANSWER_GET_ALL_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(answers)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 답변 단건 조회 API
     *
     * @param questionId    질문 ID
     * @param answerId      답변 ID
     * @return  해당 답변의 응답 데이터
     */
    @GetMapping("{answerId}")
    public ResponseEntity<ApiResponse> getAnswer(@PathVariable Long questionId,
        @PathVariable("answerId") Long answerId)
    {
        AnswerResponseDto responseDto = answerService.getAnswer(questionId, answerId);
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.ANSWER_GET_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/count")
    public ResponseEntity<ApiResponse> countUserAnswers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        int answerCount = answerService.countUserAnswers(userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.ANSWER_GET_COUNT.format())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(answerCount)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/list")
    public ResponseEntity<ApiResponse> listUserAnswers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> answers = answerService.listUserAnswers(userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.ANSWER_GET_LIST.format())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(answers)
            .build();
        return ResponseEntity.ok(response);
    }
}
