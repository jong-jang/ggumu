package com.jjh.ggumu.domain.follow.controller;

import com.jjh.ggumu.common.response.ApiResponse;
import com.jjh.ggumu.domain.follow.dto.FollowUserResponse;
import com.jjh.ggumu.domain.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Follow", description = "팔로우 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우")
    @PostMapping("/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID targetUserId) {
        followService.follow(UUID.fromString(userId), targetUserId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "언팔로우")
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID targetUserId) {
        followService.unfollow(UUID.fromString(userId), targetUserId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "팔로워 목록 조회")
    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<List<FollowUserResponse>>> getFollowers(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(followService.getFollowers(UUID.fromString(userId))));
    }

    @Operation(summary = "팔로잉 목록 조회")
    @GetMapping("/followings")
    public ResponseEntity<ApiResponse<List<FollowUserResponse>>> getFollowings(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(followService.getFollowings(UUID.fromString(userId))));
    }
}
