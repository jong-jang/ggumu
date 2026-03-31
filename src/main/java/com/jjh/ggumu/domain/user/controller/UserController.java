package com.jjh.ggumu.domain.user.controller;

import com.jjh.ggumu.common.response.ApiResponse;
import com.jjh.ggumu.domain.user.dto.OnboardingRequest;
import com.jjh.ggumu.domain.user.dto.UserResponse;
import com.jjh.ggumu.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "사용자 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMe(UUID.fromString(userId))));
    }

    @Operation(summary = "온보딩 완료", description = "닉네임 등 초기 프로필 정보를 설정합니다.")
    @PostMapping("/onboarding")
    public ResponseEntity<ApiResponse<Void>> onboarding(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody OnboardingRequest request) {
        userService.completeOnboarding(UUID.fromString(userId), request);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
