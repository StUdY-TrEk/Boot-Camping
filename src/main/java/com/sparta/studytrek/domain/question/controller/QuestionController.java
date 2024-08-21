package com.sparta.studytrek.domain.question.controller;

import java.util.List;

import com.sparta.studytrek.common.ApiResponse;
import com.sparta.studytrek.common.ResponseText;
import com.sparta.studytrek.domain.question.dto.QuestionRequestDto;
import com.sparta.studytrek.domain.question.dto.QuestionResponseDto;
import com.sparta.studytrek.domain.question.service.QuestionService;
import com.sparta.studytrek.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 질문 작성 API
     *
     * @param requestDto  질문 작성 데이터
     * @param userDetails 인증된 유저 정보
     * @return 질문 작성 응답 데이터
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createQuestion(@RequestBody QuestionRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        QuestionResponseDto responseDto = questionService.createQuestion(requestDto,
            userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.QUESTION_CREATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.CREATED.value()))
            .data(responseDto)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 질문 수정 API
     *
     * @param id          질문 ID
     * @param requestDto  질문 수정 데이터
     * @param userDetails 인증된 유저 정보
     * @return 질문 수정 응답 데이터
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateQuestion(@PathVariable Long id,
        @RequestBody QuestionRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        QuestionResponseDto responseDto = questionService.updateQuestion(id, requestDto,
            userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.QUESTION_UPDATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 질문 삭제 API
     *
     * @param id          질문 ID
     * @param userDetails 인증된 유저 정보
     * @return 질문 삭제 응답 데이터
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteQuestion(@PathVariable Long id,
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        questionService.deleteQuestion(id, userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.QUESTION_DELETE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.NO_CONTENT.value()))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 질문 전체 조회 API
     *
     * @param pageable 페이지 정보
     * @return 질문 전체 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllQuestions(Pageable pageable)
    {
        Page<QuestionResponseDto> responseDtos = questionService.getQuestions(pageable);
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.QUESTION_GET_ALL_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDtos)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 질문 단건 조회
     *
     * @param id 질문 ID
     * @return 해당 질문의 응답 데이터
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getQuestion(@PathVariable Long id)
    {
        QuestionResponseDto responseDto = questionService.getQuestion(id);
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.QUESTION_GET_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(responseDto)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/count")
    public ResponseEntity<ApiResponse> countUserQuestions(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        int questionCount = questionService.countUserQuestions(userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.QUESTION_GET_COUNT.format())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(questionCount)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/list")
    public ResponseEntity<ApiResponse> listUserQuestionList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> questionList = questionService.listUserQuestions(userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.QUESTION_GET_LIST.format())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(questionList)
            .build();
        return ResponseEntity.ok(response);
    }

}
