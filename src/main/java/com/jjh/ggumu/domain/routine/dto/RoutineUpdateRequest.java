package com.jjh.ggumu.domain.routine.dto;

import jakarta.validation.constraints.NotBlank;

public record RoutineUpdateRequest(
        @NotBlank String title,
        String description,
        Boolean isPublic
) {}