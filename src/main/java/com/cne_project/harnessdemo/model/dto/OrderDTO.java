package com.cne_project.harnessdemo.model.dto;

import com.cne_project.harnessdemo.model.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Read-only projection of an order returned by the API.
 */
public record OrderDTO(
        Long id,
        List<OrderItemDTO> items,
        BigDecimal total,
        LocalDateTime createdAt,
        OrderStatus status
) {}
