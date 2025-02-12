package com.emir.gitautocommit.exception;

public class GitOperationException extends GitAutoCommitException {
    public GitOperationException(String message) {
        super(message);
    }

    public GitOperationException(String message, Throwable cause) {
        super(message, cause);
    }
} 