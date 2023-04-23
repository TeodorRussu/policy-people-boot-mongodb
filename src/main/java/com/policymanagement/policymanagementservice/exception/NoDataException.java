package com.policymanagement.policymanagementservice.exception;

public class NoDataException extends RuntimeException {

    public static final String MESSAGE = "There is no policy for the requested data";

    public NoDataException() {
        super(MESSAGE);
    }
}
