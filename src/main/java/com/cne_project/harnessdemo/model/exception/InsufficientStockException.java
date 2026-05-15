package com.cne_project.harnessdemo.model.exception;

public class InsufficientStockException extends DomainException {

    public InsufficientStockException(Long productId, int requested, int available) {
        super(String.format("Insufficient stock for product id %d: requested %d, available %d",
                productId, requested, available));
    }
}
