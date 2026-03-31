package com.jjh.ggumu.domain.follow.service;

import com.jjh.ggumu.domain.follow.dto.FollowUserResponse;
import com.jjh.ggumu.domain.follow.entity.Follow;
import com.jjh.ggumu.domain.follow.repository.FollowRepository;
import com.jjh.ggumu.domain.routine.dto.RoutineResponse;
import com.jjh.ggumu.domain.routine.entity.Routine;
import com.jjh.ggumu.domain.routine.repository.RoutineRepository;
import com.jjh.ggumu.domain.user.entity.User;
import com.jjh.ggumu.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoutineRepository routineRepository;

    @InjectMocks
    private FollowService followService;

    private UUID followerId;
    private UUID followingId;
    private User follower;
    private User following;

    @BeforeEach
    void setUp() {
        followerId = UUID.randomUUID();
        followingId = UUID.randomUUID();
        follower = User.ofKakao("kakao-1", "팔로워", "http://img.com/a.jpg");
        following = User.ofKakao("kakao-2", "팔로잉", "http://img.com/b.jpg");
        ReflectionTestUtils.setField(follower, "id", followerId);
        ReflectionTestUtils.setField(following, "id", followingId);
    }

    @Test
    void follow_팔로우_성공() {
        given(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).willReturn(false);
        given(userRepository.findById(followerId)).willReturn(Optional.of(follower));
        given(userRepository.findById(followingId)).willReturn(Optional.of(following));

        followService.follow(followerId, followingId);

        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void follow_자기자신_예외() {
        assertThatThrownBy(() -> followService.follow(followerId, followerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자기 자신을 팔로우할 수 없습니다.");
    }

    @Test
    void follow_이미팔로우_예외() {
        given(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).willReturn(true);

        assertThatThrownBy(() -> followService.follow(followerId, followingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 팔로우한 사용자입니다.");
    }

    @Test
    void unfollow_언팔로우_성공() {
        given(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).willReturn(true);

        followService.unfollow(followerId, followingId);

        verify(followRepository).deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Test
    void unfollow_팔로우안한사용자_예외() {
        given(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)).willReturn(false);

        assertThatThrownBy(() -> followService.unfollow(followerId, followingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("팔로우하지 않은 사용자입니다.");
    }

    @Test
    void getFollowers_팔로워목록_반환() {
        Follow follow = Follow.of(follower, following);
        given(followRepository.findByFollowingId(followingId)).willReturn(List.of(follow));

        List<FollowUserResponse> result = followService.getFollowers(followingId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nickname()).isEqualTo("팔로워");
    }

    @Test
    void getFollowings_팔로잉목록_반환() {
        Follow follow = Follow.of(follower, following);
        given(followRepository.findByFollowerId(followerId)).willReturn(List.of(follow));

        List<FollowUserResponse> result = followService.getFollowings(followerId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nickname()).isEqualTo("팔로잉");
    }

    @Test
    void getFeed_팔로잉없으면_빈목록() {
        given(followRepository.findByFollowerId(followerId)).willReturn(List.of());

        List<RoutineResponse> result = followService.getFeed(followerId);

        assertThat(result).isEmpty();
    }

    @Test
    void getFeed_팔로잉루틴_반환() {
        Follow follow = Follow.of(follower, following);
        Routine routine = Routine.create(following, "팔로잉 루틴", "설명", true);
        given(followRepository.findByFollowerId(followerId)).willReturn(List.of(follow));
        given(routineRepository.findFeedByUserIds(List.of(followingId))).willReturn(List.of(routine));

        List<RoutineResponse> result = followService.getFeed(followerId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("팔로잉 루틴");
    }
}
