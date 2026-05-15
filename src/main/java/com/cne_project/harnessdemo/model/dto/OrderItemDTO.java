package com.cne_project.harnessdemo.model.dto;

import java.math.BigDecimal;

public record OrderItemDTO(Long productId, int quantity, BigDecimal unitPrice) {}
