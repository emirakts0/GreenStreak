package com.emir.gitautocommit.exception;

public class GitAutoCommitException extends RuntimeException {
    public GitAutoCommitException(String message) {
        super(message);
    }

    public GitAutoCommitException(String message, Throwable cause) {
        super(message, cause);
    }
} 