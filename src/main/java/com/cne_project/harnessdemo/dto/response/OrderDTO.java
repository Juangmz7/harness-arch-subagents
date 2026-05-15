package com.cne_project.harnessdemo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,
        List<OrderItemDTO> items,
        BigDecimal total,
        LocalDateTime createdAt,
        String status
) {}
