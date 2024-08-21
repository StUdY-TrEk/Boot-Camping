package com.sparta.studytrek.domain.like.controller;

import java.util.List;

import com.sparta.studytrek.common.ApiResponse;
import com.sparta.studytrek.common.ResponseText;
import com.sparta.studytrek.domain.camp.service.CampService;
import com.sparta.studytrek.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/camps")
@RequiredArgsConstructor
public class CampLikeController {

    private final CampService campService;

    /**
     * 캠프에 좋아요를 추가하는 API
     *
     * @param campId 캠프 ID
     * @param userDetails 인증된 유저 정보
     * @return 좋아요 수를 포함한 성공 메시지를 담고 있는 ApiResponse 객체를 포함한 응답
     */
    @PostMapping("/{campId}/like")
    public ResponseEntity<ApiResponse<String>> likeCamp(@PathVariable Long campId,
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        int campAllLike = campService.likeCamp(campId, userDetails.getUser());
        ApiResponse<String> response = ApiResponse.<String>builder()
            .msg(ResponseText.LIKE_RECRUIT_SUCCESS.format(campAllLike))
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 캠프에 대한 좋아요를 취소하는 API
     *
     * @param campId 캠프 ID
     * @param userDetails 인증된 유저 정보
     * @return 좋아요 수를 포함한 성공 메시지를 담고 있는 ApiResponse 객체를 포함한 응답
     */
    @DeleteMapping("/{campId}/like")
    public ResponseEntity<ApiResponse<String>> unlikeCamp(@PathVariable Long campId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int campAllLike = campService.unlikeCamp(campId, userDetails.getUser());
        ApiResponse<String> response = ApiResponse.<String>builder()
            .msg(ResponseText.LIKE_CALL_OFF_SUCCESS.format(campAllLike))
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/like/count")
    public ResponseEntity<ApiResponse> getCampLikeCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        int likeCount = campService.getLikedCampsCount(userDetails.getUser());

        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.GET_LIKE_CAMP_COUNT.format())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(likeCount)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/like/list")
    public ResponseEntity<ApiResponse> getLikedCampsList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> likedCamps = campService.getLikedCamps(userDetails.getUser());
        ApiResponse response = ApiResponse.builder()
            .msg(ResponseText.GET_LIKE_CAMP_LIST.format())
            .statuscode(String.valueOf(HttpStatus.OK.value()))
            .data(likedCamps)
            .build();
        return ResponseEntity.ok(response);
    }
}