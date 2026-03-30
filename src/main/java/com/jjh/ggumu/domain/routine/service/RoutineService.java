package com.jjh.ggumu.domain.routine.service;

import com.jjh.ggumu.domain.routine.dto.*;
import com.jjh.ggumu.domain.routine.entity.Routine;
import com.jjh.ggumu.domain.routine.entity.RoutineItem;
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
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;

    @Transactional
    public RoutineResponse create(UUID userId, RoutineCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Routine routine = Routine.create(user, request.title(), request.description(), request.isPublic());

        for (int i = 0; i < request.items().size(); i++) {
            RoutineItem item = RoutineItem.create(routine, request.items().get(i), i + 1);
            routine.getItems().add(item);
        }

        return RoutineResponse.from(routineRepository.save(routine));
    }

    public List<RoutineResponse> getMyRoutines(UUID userId) {
        return routineRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(RoutineResponse::from).toList();
    }

    public RoutineResponse getRoutine(UUID routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));
        return RoutineResponse.from(routine);
    }

    @Transactional
    public RoutineResponse update(UUID userId, UUID routineId, RoutineUpdateRequest request) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));

        if (!routine.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        routine.update(request.title(), request.description(), request.isPublic());
        return RoutineResponse.from(routine);
    }

    @Transactional
    public void delete(UUID userId, UUID routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));

        if (!routine.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        routineRepository.delete(routine);
    }
}
