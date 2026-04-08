package com.jjh.ggumu.domain.routine.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RoutineCreateRequest(
        @NotBlank String title,
        String description,
        Boolean isPublic,
        List<RoutineItemRequest> items
) {}