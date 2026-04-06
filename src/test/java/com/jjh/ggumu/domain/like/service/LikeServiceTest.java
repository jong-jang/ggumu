package com.jjh.ggumu.domain.like.service;

import com.jjh.ggumu.domain.like.entity.RoutineLike;
import com.jjh.ggumu.domain.like.repository.RoutineLikeRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock private RoutineLikeRepository likeRepository;
    @Mock private RoutineRepository routineRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private LikeService likeService;

    private UUID userId;
    private UUID routineId;
    private User user;
    private Routine routine;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        routineId = UUID.randomUUID();
        user = User.ofKakao("kakao-123", "테스터", "http://img.com/photo.jpg");
        ReflectionTestUtils.setField(user, "id", userId);
        routine = Routine.create(user, "아침 루틴", "설명", true);
        ReflectionTestUtils.setField(routine, "id", routineId);
    }

    @Test
    void like_좋아요_성공() {
        given(likeRepository.existsByUserIdAndRoutineId(userId, routineId)).willReturn(false);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(routineRepository.findById(routineId)).willReturn(Optional.of(routine));

        likeService.like(userId, routineId);

        verify(likeRepository).save(any(RoutineLike.class));
    }

    @Test
    void like_중복좋아요_예외() {
        given(likeRepository.existsByUserIdAndRoutineId(userId, routineId)).willReturn(true);

        assertThatThrownBy(() -> likeService.like(userId, routineId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 좋아요한 루틴입니다.");
    }

    @Test
    void unlike_좋아요취소_성공() {
        given(likeRepository.existsByUserIdAndRoutineId(userId, routineId)).willReturn(true);
        given(routineRepository.findById(routineId)).willReturn(Optional.of(routine));

        likeService.unlike(userId, routineId);

        verify(likeRepository).deleteByUserIdAndRoutineId(userId, routineId);
    }

    @Test
    void unlike_좋아요안한루틴_예외() {
        given(likeRepository.existsByUserIdAndRoutineId(userId, routineId)).willReturn(false);

        assertThatThrownBy(() -> likeService.unlike(userId, routineId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("좋아요하지 않은 루틴입니다.");
    }
}
