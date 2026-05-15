package com.cne_project.harnessdemo.dto.response;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stock
) {}
