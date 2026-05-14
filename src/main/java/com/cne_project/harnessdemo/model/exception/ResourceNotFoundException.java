package com.cne_project.harnessdemo.model.exception;

/**
 * Thrown when a requested resource cannot be found in the persistence layer.
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s not found with id: %d", resourceName, id));
    }
}
