package com.cne_project.harnessdemo.model.dto;

import java.math.BigDecimal;

/**
 * Immutable DTO representing a product returned by the API.
 */
public record ProductDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stock
) {
}
