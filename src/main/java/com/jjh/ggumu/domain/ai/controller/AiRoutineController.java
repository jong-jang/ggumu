package com.jjh.ggumu.domain.ai.controller;

import com.jjh.ggumu.common.response.ApiResponse;
import com.jjh.ggumu.domain.ai.dto.AiRoutineRecommendResponse;
import com.jjh.ggumu.domain.ai.service.AiRoutineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "AI", description = "AI 루틴 추천 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/ai/routines")
@RequiredArgsConstructor
public class AiRoutineController {

    private final AiRoutineService aiRoutineService;

    @Operation(summary = "AI 루틴 추천", description = "온보딩 설문 결과를 기반으로 아침 루틴 5가지를 추천합니다.")
    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<AiRoutineRecommendResponse>> recommend(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(
                new AiRoutineRecommendResponse(aiRoutineService.recommend(UUID.fromString(userId)))
        ));
    }
}
