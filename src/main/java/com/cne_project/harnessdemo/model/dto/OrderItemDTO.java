package com.cne_project.harnessdemo.model.dto;

import java.math.BigDecimal;

/**
 * Read-only projection of a single order line item returned in the API response.
 */
public record OrderItemDTO(Long productId, int quantity, BigDecimal unitPrice) {}
