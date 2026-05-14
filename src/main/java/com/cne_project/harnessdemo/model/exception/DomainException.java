package com.cne_project.harnessdemo.model.exception;

/**
 * Base class for all domain-level exceptions in this application.
 * Extends RuntimeException to ensure unchecked propagation.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
