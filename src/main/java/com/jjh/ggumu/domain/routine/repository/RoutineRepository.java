package com.jjh.ggumu.domain.routine.repository;

import com.jjh.ggumu.domain.routine.entity.Routine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoutineRepository extends JpaRepository<Routine, UUID> {
    List<Routine> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Page<Routine> findByIsPublicTrueOrderByLikeCountDescViewCountDesc(Pageable pageable);
}