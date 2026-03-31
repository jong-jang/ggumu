package com.jjh.ggumu.domain.routine.service;

import com.jjh.ggumu.domain.routine.dto.RoutineCreateRequest;
import com.jjh.ggumu.domain.routine.dto.RoutineResponse;
import com.jjh.ggumu.domain.routine.dto.RoutineUpdateRequest;
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
class RoutineServiceTest {

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoutineService routineService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.ofKakao("kakao-123", "테스터", "http://img.com/photo.jpg");
        ReflectionTestUtils.setField(user, "id", userId);
    }

    @Test
    void create_루틴생성_성공() {
        RoutineCreateRequest request = new RoutineCreateRequest("아침 루틴", "설명", true, List.of("물 마시기", "스트레칭"));
        Routine routine = Routine.create(user, request.title(), request.description(), request.isPublic());

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(routineRepository.save(any(Routine.class))).willReturn(routine);

        RoutineResponse response = routineService.create(userId, request);

        assertThat(response.title()).isEqualTo("아침 루틴");
        assertThat(response.isPublic()).isTrue();
        verify(routineRepository).save(any(Routine.class));
    }

    @Test
    void create_존재하지않는사용자_예외() {
        RoutineCreateRequest request = new RoutineCreateRequest("루틴", null, false, List.of());

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> routineService.create(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    void getMyRoutines_내루틴목록_반환() {
        Routine r1 = Routine.create(user, "루틴A", null, false);
        Routine r2 = Routine.create(user, "루틴B", null, true);

        given(routineRepository.findByUserIdOrderByCreatedAtDesc(userId)).willReturn(List.of(r1, r2));

        List<RoutineResponse> result = routineService.getMyRoutines(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("루틴A");
    }

    @Test
    void getRoutine_루틴조회_성공() {
        UUID routineId = UUID.randomUUID();
        Routine routine = Routine.create(user, "루틴", "설명", false);

        given(routineRepository.findById(routineId)).willReturn(Optional.of(routine));

        RoutineResponse response = routineService.getRoutine(routineId);

        assertThat(response.title()).isEqualTo("루틴");
    }

    @Test
    void getRoutine_존재하지않는루틴_예외() {
        UUID routineId = UUID.randomUUID();

        given(routineRepository.findById(routineId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> routineService.getRoutine(routineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("루틴을 찾을 수 없습니다.");
    }

    @Test
    void update_루틴수정_성공() {
        UUID routineId = UUID.randomUUID();
        Routine routine = Routine.create(user, "기존 제목", null, false);
        RoutineUpdateRequest request = new RoutineUpdateRequest("수정된 제목", "수정된 설명", true);

        given(routineRepository.findById(routineId)).willReturn(Optional.of(routine));

        RoutineResponse response = routineService.update(userId, routineId, request);

        assertThat(response.title()).isEqualTo("수정된 제목");
        assertThat(response.isPublic()).isTrue();
    }

    @Test
    void update_권한없음_예외() {
        UUID routineId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID(); // userId와 다른 UUID
        Routine routine = Routine.create(user, "루틴", null, false);
        RoutineUpdateRequest request = new RoutineUpdateRequest("수정", null, false);

        given(routineRepository.findById(routineId)).willReturn(Optional.of(routine));

        assertThatThrownBy(() -> routineService.update(otherUserId, routineId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수정 권한이 없습니다.");
    }

    @Test
    void delete_루틴삭제_성공() {
        UUID routineId = UUID.randomUUID();
        Routine routine = Routine.create(user, "루틴", null, false);

        given(routineRepository.findById(routineId)).willReturn(Optional.of(routine));

        routineService.delete(userId, routineId);

        verify(routineRepository).delete(routine);
    }

    @Test
    void delete_권한없음_예외() {
        UUID routineId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID(); // userId와 다른 UUID
        Routine routine = Routine.create(user, "루틴", null, false);

        given(routineRepository.findById(routineId)).willReturn(Optional.of(routine));

        assertThatThrownBy(() -> routineService.delete(otherUserId, routineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("삭제 권한이 없습니다.");
    }
}
