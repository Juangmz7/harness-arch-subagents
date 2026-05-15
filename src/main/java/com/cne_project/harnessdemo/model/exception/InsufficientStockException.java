package com.cne_project.harnessdemo.model.exception;

/**
 * Thrown when a requested quantity exceeds the available stock for a product.
 * Extends {@link DomainException} (which extends {@link RuntimeException}) so
 * Spring's @Transactional rolls back automatically without needing rollbackFor.
 */
public class InsufficientStockException extends DomainException {

    public InsufficientStockException(Long productId, int requested, int available) {
        super(String.format(
                "Insufficient stock for product %d: requested %d but only %d available",
                productId, requested, available));
    }
}
