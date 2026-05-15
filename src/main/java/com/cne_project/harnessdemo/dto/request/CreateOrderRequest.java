package com.cne_project.harnessdemo.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull(message = "items must not be null")
        @NotEmpty(message = "items must not be empty")
        @Valid
        List<OrderItemRequest> items
) {}
