package com.jjh.ggumu.domain.routine.controller;

import com.jjh.ggumu.common.response.ApiResponse;
import com.jjh.ggumu.domain.routine.dto.*;
import com.jjh.ggumu.domain.routine.service.RoutineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoutineResponse>> create(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody RoutineCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(routineService.create(UUID.fromString(userId), request)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<RoutineResponse>>> getMyRoutines(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(routineService.getMyRoutines(UUID.fromString(userId))));
    }

    @GetMapping("/{routineId}")
    public ResponseEntity<ApiResponse<RoutineResponse>> getRoutine(@PathVariable UUID routineId) {
        return ResponseEntity.ok(ApiResponse.ok(routineService.getRoutine(routineId)));
    }

    @PutMapping("/{routineId}")
    public ResponseEntity<ApiResponse<RoutineResponse>> update(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID routineId,
            @Valid @RequestBody RoutineUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(routineService.update(UUID.fromString(userId), routineId, request)));
    }

    @DeleteMapping("/{routineId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID routineId) {
        routineService.delete(UUID.fromString(userId), routineId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
