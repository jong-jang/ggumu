package com.jjh.ggumu.domain.follow.dto;

import com.jjh.ggumu.domain.user.entity.User;

import java.util.UUID;

public record FollowUserResponse(
        UUID id,
        String nickname,
        String profileImageUrl
) {
    public static FollowUserResponse from(User user) {
        return new FollowUserResponse(user.getId(), user.getNickname(), user.getProfileImageUrl());
    }
}