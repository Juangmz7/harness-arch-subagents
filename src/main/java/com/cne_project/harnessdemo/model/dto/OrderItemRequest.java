package com.cne_project.harnessdemo.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Input DTO for a single line item within a create-order request.
 */
public record OrderItemRequest(
        @NotNull(message = "productId must not be null")
        Long productId,

        @Min(value = 1, message = "quantity must be at least 1")
        int quantity
) {}
