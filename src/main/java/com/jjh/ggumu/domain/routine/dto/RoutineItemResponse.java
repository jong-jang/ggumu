package com.jjh.ggumu.domain.routine.dto;

import com.jjh.ggumu.domain.routine.entity.RoutineItem;

import java.util.UUID;

public record RoutineItemResponse(UUID id, String title, int orderNum) {
    public static RoutineItemResponse from(RoutineItem item) {
        return new RoutineItemResponse(item.getId(), item.getTitle(), item.getOrderNum());
    }
}