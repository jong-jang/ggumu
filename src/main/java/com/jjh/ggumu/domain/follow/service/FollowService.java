package com.jjh.ggumu.domain.follow.service;

import com.jjh.ggumu.domain.follow.dto.FollowUserResponse;
import com.jjh.ggumu.domain.follow.entity.Follow;
import com.jjh.ggumu.domain.follow.repository.FollowRepository;
import com.jjh.ggumu.domain.routine.dto.RoutineResponse;
import com.jjh.ggumu.domain.routine.repository.RoutineRepository;
import com.jjh.ggumu.domain.user.entity.User;
import com.jjh.ggumu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;

    @Transactional
    public void follow(UUID followerId, UUID targetUserId) {
        if (followerId.equals(targetUserId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, targetUserId)) {
            throw new IllegalArgumentException("이미 팔로우한 사용자입니다.");
        }
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다."));

        followRepository.save(Follow.of(follower, following));
    }

    @Transactional
    public void unfollow(UUID followerId, UUID targetUserId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, targetUserId)) {
            throw new IllegalArgumentException("팔로우하지 않은 사용자입니다.");
        }
        followRepository.deleteByFollowerIdAndFollowingId(followerId, targetUserId);
    }

    public List<FollowUserResponse> getFollowers(UUID userId) {
        return followRepository.findByFollowingId(userId)
                .stream()
                .map(f -> FollowUserResponse.from(f.getFollower()))
                .toList();
    }

    public List<FollowUserResponse> getFollowings(UUID userId) {
        return followRepository.findByFollowerId(userId)
                .stream()
                .map(f -> FollowUserResponse.from(f.getFollowing()))
                .toList();
    }

    public List<RoutineResponse> getFeed(UUID userId) {
        List<UUID> followingIds = followRepository.findByFollowerId(userId)
                .stream()
                .map(f -> f.getFollowing().getId())
                .toList();

        if (followingIds.isEmpty()) {
            return List.of();
        }

        return routineRepository.findFeedByUserIds(followingIds)
                .stream()
                .map(RoutineResponse::from)
                .toList();
    }
}