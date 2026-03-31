package com.jjh.ggumu.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(
        @NotBlank String refreshToken
) {}
