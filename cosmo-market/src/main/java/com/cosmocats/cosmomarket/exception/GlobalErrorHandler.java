package com.cosmocats.cosmomarket.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalErrorHandler {

    private ResponseEntity<ErrorRecord> buildResponse(HttpStatus status, String message, String path) {
        ErrorRecord body = new ErrorRecord(status.value(), status.getReasonPhrase(), message, path);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRecord> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        var errors = exception.getBindingResult().getFieldErrors();
        var error = errors.isEmpty() ? null : errors.get(0);
        String message = (error == null)
                ? "Validation failed"
                : "Validation failed for object '%s': field '%s' %s".formatted(error.getObjectName(), error.getField(), error.getDefaultMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorRecord> handleConstraint(ConstraintViolationException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRecord> handleNotReadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Incorrect JSON body: %s".formatted(exception.getMessage()), request.getRequestURI());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorRecord> handleNotFound(NoSuchElementException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRecord> handleAnyException(Exception exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request.getRequestURI());
    }
}
