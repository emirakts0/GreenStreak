package com.emir.gitautocommit.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ExceptionResponse(
        String message,
        HttpStatus httpStatus,
        LocalDateTime timeStamp) {
}
