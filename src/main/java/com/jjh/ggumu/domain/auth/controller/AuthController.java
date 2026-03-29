package com.jjh.ggumu.domain.auth.controller;

import com.jjh.ggumu.common.response.ApiResponse;
import com.jjh.ggumu.domain.auth.dto.ReissueRequest;
import com.jjh.ggumu.domain.auth.dto.TokenResponse;
import com.jjh.ggumu.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@Valid @RequestBody ReissueRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.reissue(request.refreshToken())));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String bearer) {
        String token = bearer.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
