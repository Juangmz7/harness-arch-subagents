package com.cne_project.harnessdemo.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Input DTO for creating a new order. The list of items must contain at least
 * one entry and each item must itself be valid.
 */
public record CreateOrderRequest(
        @NotNull(message = "items must not be null")
        @NotEmpty(message = "items must not be empty")
        @Valid
        List<OrderItemRequest> items
) {}
