package com.cne_project.harnessdemo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "productId must not be null")
        Long productId,
        @Min(value = 1, message = "quantity must be at least 1")
        int quantity
) {}
