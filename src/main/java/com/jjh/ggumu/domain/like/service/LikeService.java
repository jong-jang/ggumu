package com.jjh.ggumu.domain.like.service;

import com.jjh.ggumu.domain.like.entity.RoutineLike;
import com.jjh.ggumu.domain.like.repository.RoutineLikeRepository;
import com.jjh.ggumu.domain.routine.dto.RoutineResponse;
import com.jjh.ggumu.domain.routine.entity.Routine;
import com.jjh.ggumu.domain.routine.repository.RoutineRepository;
import com.jjh.ggumu.domain.user.entity.User;
import com.jjh.ggumu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final RoutineLikeRepository likeRepository;
    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;

    @Transactional
    public void like(UUID userId, UUID routineId) {
        if (likeRepository.existsByUserIdAndRoutineId(userId, routineId)) {
            throw new IllegalStateException("이미 좋아요한 루틴입니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));

        likeRepository.save(RoutineLike.of(user, routine));
        routine.incrementLikeCount();
    }

    @Transactional
    public void unlike(UUID userId, UUID routineId) {
        if (!likeRepository.existsByUserIdAndRoutineId(userId, routineId)) {
            throw new IllegalStateException("좋아요하지 않은 루틴입니다.");
        }
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));

        likeRepository.deleteByUserIdAndRoutineId(userId, routineId);
        routine.decrementLikeCount();
    }

    @Transactional(readOnly = true)
    public Page<RoutineResponse> getRanking(Pageable pageable) {
        return routineRepository.findByIsPublicTrueOrderByLikeCountDescViewCountDesc(pageable)
                .map(RoutineResponse::from);
    }
}
