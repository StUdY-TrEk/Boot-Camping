package com.sparta.studytrek.domain.study.controller;

import java.util.List;

import com.sparta.studytrek.aop.StudyRoleCheck;
import com.sparta.studytrek.common.ApiResponse;
import com.sparta.studytrek.common.ResponseText;
import com.sparta.studytrek.domain.study.dto.StudyRequestDto;
import com.sparta.studytrek.domain.study.dto.StudyResponseDto;
import com.sparta.studytrek.domain.study.service.StudyService;
import com.sparta.studytrek.security.UserDetailsImpl;
import jakarta.validation.Valid;
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
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    /**
     * 스터디 모집글 작성 API
     *
     * @param request     스터디 모집글 작성 데이터
     * @param userDetails 인증된 유저 정보
     * @return 스터디 모집글 작성 응답 데이터
     */
    @PostMapping
    @StudyRoleCheck
    public ResponseEntity<ApiResponse<StudyResponseDto>> createStudy(
        @Valid @RequestBody StudyRequestDto request,
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        StudyResponseDto responseDtos = studyService.createStudy(request, userDetails.getUser());
        ApiResponse<StudyResponseDto> apiResponse = ApiResponse.<StudyResponseDto>builder()
            .msg(ResponseText.STUDY_CREATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.CREATED.value()))
            .data(responseDtos)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * 스터디 모집글 전체 조회 (페이징 처리) API
     *
     * @param pageable 페이지 정보
     * @return 스터디 모집글 페이징 조회 데이터
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudyResponseDto>>> getAllStudies(Pageable pageable)
    {
        Page<StudyResponseDto> response = studyService.getAllStudies(pageable);
        ApiResponse<Page<StudyResponseDto>> apiResponse = ApiResponse.<Page<StudyResponseDto>>builder()
            .msg(ResponseText.STUDY_GET_ALL_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(response)
            .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 스터디 모집글 단건 조회 API
     *
     * @param studyId 조회할 스터디 모집글 ID
     * @return 스터디 모집글 응답 데이터
     */
    @GetMapping("/{studyId}")
    public ResponseEntity<ApiResponse<StudyResponseDto>> getStudy(@PathVariable Long studyId)
    {
        StudyResponseDto study = studyService.getStudy(studyId);
        ApiResponse<StudyResponseDto> apiResponse = ApiResponse.<StudyResponseDto>builder()
            .msg(ResponseText.STUDY_GET_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(study)
            .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/user/count")
    public ResponseEntity<ApiResponse> countUserStudies(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        int studyCount = studyService.countUserStudies(userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.STUDY_GET_COUNT.format())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(studyCount)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/list")
    public ResponseEntity<ApiResponse> listUserStudies(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> studies = studyService.listUserStudies(userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.STUDY_GET_LIST.format())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(studies)
            .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 스터디 모집글 수정 API
     *
     * @param studyId     수정할 스터디 모집글 ID
     * @param request     스터디 모집글 수정 데이터
     * @param userDetails 인증된 유저 정보
     * @return 수정된 스터디 모집글 응답 데이터
     */
    @PutMapping("/{studyId}")
    @StudyRoleCheck
    public ResponseEntity<ApiResponse<StudyResponseDto>> updateStudy(@PathVariable Long studyId,
        @Valid @RequestBody StudyRequestDto request,
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        StudyResponseDto updatedStudy = studyService.updateStudy(studyId, request,
            userDetails.getUser());
        ApiResponse<StudyResponseDto> apiResponse = ApiResponse.<StudyResponseDto>builder()
            .msg(ResponseText.STUDY_UPDATE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(updatedStudy)
            .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 스터디 모집글 삭제 API
     *
     * @param studyId     삭제할 스터디 모집글 ID
     * @param userDetails 인증된 유저 정보
     * @return 삭제 결과 응답 데이터
     */
    @DeleteMapping("/{studyId}")
    @StudyRoleCheck
    public ResponseEntity<ApiResponse<Void>> deleteStudy(@PathVariable Long studyId,
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        studyService.deleteStudy(studyId, userDetails.getUser());
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
            .msg(ResponseText.STUDY_DELETE_SUCCESS.getMsg())
            .statuscode(String.valueOf(HttpStatus.NO_CONTENT.value()))
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}