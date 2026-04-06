package com.jjh.ggumu.domain.routine.controller;

import com.jjh.ggumu.common.response.ApiResponse;
import com.jjh.ggumu.domain.follow.service.FollowService;
import com.jjh.ggumu.domain.like.service.LikeService;
import com.jjh.ggumu.domain.routine.dto.*;
import com.jjh.ggumu.domain.routine.service.RoutineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Routine", description = "루틴 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;
    private final FollowService followService;
    private final LikeService likeService;

    @Operation(summary = "루틴 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<RoutineResponse>> create(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody RoutineCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(routineService.create(UUID.fromString(userId), request)));
    }

    @Operation(summary = "피드 조회", description = "팔로잉한 사용자들의 루틴을 최신순으로 반환합니다.")
    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<List<RoutineResponse>>> getFeed(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(followService.getFeed(UUID.fromString(userId))));
    }

    @Operation(summary = "내 루틴 목록 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<RoutineResponse>>> getMyRoutines(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(routineService.getMyRoutines(UUID.fromString(userId))));
    }

    @Operation(summary = "루틴 단건 조회")
    @GetMapping("/{routineId}")
    public ResponseEntity<ApiResponse<RoutineResponse>> getRoutine(@PathVariable UUID routineId) {
        return ResponseEntity.ok(ApiResponse.ok(routineService.getRoutine(routineId)));
    }

    @Operation(summary = "루틴 수정")
    @PutMapping("/{routineId}")
    public ResponseEntity<ApiResponse<RoutineResponse>> update(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID routineId,
            @Valid @RequestBody RoutineUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(routineService.update(UUID.fromString(userId), routineId, request)));
    }

    @Operation(summary = "루틴 삭제")
    @DeleteMapping("/{routineId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID routineId) {
        routineService.delete(UUID.fromString(userId), routineId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "루틴 좋아요")
    @PostMapping("/{routineId}/like")
    public ResponseEntity<ApiResponse<Void>> like(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID routineId) {
        likeService.like(UUID.fromString(userId), routineId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "루틴 좋아요 취소")
    @DeleteMapping("/{routineId}/like")
    public ResponseEntity<ApiResponse<Void>> unlike(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID routineId) {
        likeService.unlike(UUID.fromString(userId), routineId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "랭킹 조회", description = "좋아요 + 조회수 기준 공개 루틴 랭킹 (페이징)")
    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<Page<RoutineResponse>>> getRanking(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(likeService.getRanking(pageable)));
    }
}
