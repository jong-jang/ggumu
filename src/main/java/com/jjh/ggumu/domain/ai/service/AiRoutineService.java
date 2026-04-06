package com.jjh.ggumu.domain.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjh.ggumu.domain.user.entity.User;
import com.jjh.ggumu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiRoutineService {

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String CLAUDE_MODEL = "claude-haiku-4-5-20251001";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final UserRepository userRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${claude.api-key:}")
    private String apiKey;

    public List<String> recommend(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String prompt = buildPrompt(user.getSurveyResult());
        String responseJson = callClaudeApi(prompt);
        return parseRoutines(responseJson);
    }

    private String buildPrompt(String surveyResult) {
        String base = "이 성향의 사람에게 맞는 아침 루틴 5가지를 JSON 배열로만 추천해줘. 예시: [\"루틴1\", \"루틴2\", ...]";
        if (surveyResult == null || surveyResult.isBlank()) {
            return "일반적인 사람에게 맞는 아침 루틴 5가지를 JSON 배열로만 추천해줘. 예시: [\"루틴1\", \"루틴2\", ...]";
        }
        return "다음은 사용자의 온보딩 설문 결과야:\n" + surveyResult + "\n\n" + base;
    }

    String callClaudeApi(String prompt) {
        Map<String, Object> requestBody = new java.util.LinkedHashMap<>();
        requestBody.put("model", CLAUDE_MODEL);
        requestBody.put("max_tokens", 512);
        requestBody.put("system", "당신은 건강한 생활 습관을 돕는 루틴 추천 전문가입니다. 사용자 요청에 따라 루틴을 JSON 배열 형식으로만 반환하세요.");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        return restClient.post()
                .uri(CLAUDE_API_URL)
                .headers(headers -> {
                    headers.set("x-api-key", apiKey);
                    headers.set("anthropic-version", ANTHROPIC_VERSION);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(requestBody)
                .retrieve()
                .body(String.class);
    }

    private List<String> parseRoutines(String responseJson) {
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            String text = root.path("content").get(0).path("text").asText();
            int start = text.indexOf('[');
            int end = text.lastIndexOf(']') + 1;
            if (start == -1 || end <= start) {
                return List.of();
            }
            return objectMapper.readValue(text.substring(start, end), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 파싱 실패", e);
        }
    }
}
