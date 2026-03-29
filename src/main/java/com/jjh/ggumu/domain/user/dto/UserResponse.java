package com.jjh.ggumu.domain.user.dto;

import com.jjh.ggumu.domain.user.entity.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String nickname,
        String profileImageUrl,
        String bio,
        boolean onboardingCompleted
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getBio(),
                user.isOnboardingCompleted()
        );
    }
}
