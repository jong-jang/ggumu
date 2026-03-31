package com.jjh.ggumu.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OnboardingRequest(
        @NotBlank String job,
        @NotNull String workStyle,
        @NotNull String goal,
        String concerns
) {}
