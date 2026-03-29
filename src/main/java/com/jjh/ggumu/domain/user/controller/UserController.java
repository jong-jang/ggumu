package com.jjh.ggumu.domain.user.controller;

import com.jjh.ggumu.common.response.ApiResponse;
import com.jjh.ggumu.domain.user.dto.OnboardingRequest;
import com.jjh.ggumu.domain.user.dto.UserResponse;
import com.jjh.ggumu.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMe(UUID.fromString(userId))));
    }

    @PostMapping("/onboarding")
    public ResponseEntity<ApiResponse<Void>> onboarding(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody OnboardingRequest request) {
        userService.completeOnboarding(UUID.fromString(userId), request);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
