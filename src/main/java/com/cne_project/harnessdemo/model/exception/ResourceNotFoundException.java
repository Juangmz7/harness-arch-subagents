package com.cne_project.harnessdemo.model.exception;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }
}
