package com.cne_project.harnessdemo.model.exception;

public class DuplicateProductNameException extends DomainException {

    public DuplicateProductNameException(String message) {
        super(message);
    }
}
