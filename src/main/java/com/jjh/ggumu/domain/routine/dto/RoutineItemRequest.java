package com.jjh.ggumu.domain.routine.dto;

import jakarta.validation.constraints.NotBlank;

public record RoutineItemRequest(
        @NotBlank String title
) {}
