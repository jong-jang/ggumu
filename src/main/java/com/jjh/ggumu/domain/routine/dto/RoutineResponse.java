package com.jjh.ggumu.domain.routine.dto;

import com.jjh.ggumu.domain.routine.entity.Routine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoutineResponse(
        UUID id, String title, String description,
        boolean isPublic, int viewCount, int likeCount,
        List<RoutineItemResponse> items, LocalDateTime createdAt
) {
    public static RoutineResponse from(Routine routine) {
        return new RoutineResponse(
                routine.getId(), routine.getTitle(), routine.getDescription(),
                routine.isPublic(), routine.getViewCount(), routine.getLikeCount(),
                routine.getItems().stream().map(RoutineItemResponse::from).toList(),
                routine.getCreatedAt()
        );
    }
}
