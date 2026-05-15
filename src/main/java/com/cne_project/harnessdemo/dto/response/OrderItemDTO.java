package com.cne_project.harnessdemo.dto.response;

import java.math.BigDecimal;

public record OrderItemDTO(
        Long productId,
        int quantity,
        BigDecimal unitPrice
) {}
