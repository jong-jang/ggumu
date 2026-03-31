package com.jjh.ggumu.domain.follow.controller;

import com.jjh.ggumu.common.response.ApiResponse;
import com.jjh.ggumu.domain.follow.dto.FollowUserResponse;
import com.jjh.ggumu.domain.follow.service.FollowService;
import com.jjh.ggumu.domain.routine.dto.RoutineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID targetUserId) {
        followService.follow(UUID.fromString(userId), targetUserId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID targetUserId) {
        followService.unfollow(UUID.fromString(userId), targetUserId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<List<FollowUserResponse>>> getFollowers(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(followService.getFollowers(UUID.fromString(userId))));
    }

    @GetMapping("/followings")
    public ResponseEntity<ApiResponse<List<FollowUserResponse>>> getFollowings(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(followService.getFollowings(UUID.fromString(userId))));
    }
}