package com.policymanagement.policymanagementservice.exception;

public class InvalidDateException extends RuntimeException {
    public static final String MESSAGE = "The date provided in the request does not follow the format 'dd.MM.yyyy'";

    public InvalidDateException() {
        super(MESSAGE);
    }
}
