package com.jjh.ggumu.domain.like.repository;

import com.jjh.ggumu.domain.like.entity.RoutineLike;
import com.jjh.ggumu.domain.like.entity.RoutineLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoutineLikeRepository extends JpaRepository<RoutineLike, RoutineLikeId> {
    boolean existsByUserIdAndRoutineId(UUID userId, UUID routineId);
    void deleteByUserIdAndRoutineId(UUID userId, UUID routineId);
}
