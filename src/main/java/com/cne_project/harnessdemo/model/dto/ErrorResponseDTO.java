package com.cne_project.harnessdemo.model.dto;

import java.time.LocalDateTime;

/**
 * Immutable DTO for structured API error responses.
 */
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
