package com.jjh.ggumu.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjh.ggumu.domain.user.dto.OnboardingRequest;
import com.jjh.ggumu.domain.user.dto.UserResponse;
import com.jjh.ggumu.domain.user.entity.User;
import com.jjh.ggumu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserResponse getMe(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    @Transactional
    public void completeOnboarding(UUID userId, OnboardingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        try {
            String surveyJson = objectMapper.writeValueAsString(request);
            user.completeOnboarding(surveyJson);
        } catch (Exception e) {
            throw new RuntimeException("온보딩 저장 실패", e);
        }
    }
}
