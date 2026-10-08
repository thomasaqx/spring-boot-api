package com.github.thomasaqx.MedVoll.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record DTOAuth(
        @NotBlank
        String email,
        @NotBlank
        String senha
) {
}
