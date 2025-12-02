package com.cosmocats.cosmomarket.exception;

import java.util.List;
import java.util.NoSuchElementException;
import com.cosmocats.cosmomarket.featuretoggle.exception.FeatureNotAvailableException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import static java.net.URI.create;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldError> errors = exception.getBindingResult().getFieldErrors();
        var error = errors.isEmpty() ? null : errors.get(0);
        String message = (error == null)
                ? "Validation failed"
                : "Validation failed for object '%s': field '%s' %s".formatted(error.getObjectName(), error.getField(), error.getDefaultMessage());
        ProblemDetail problemDetail = forStatusAndDetail(BAD_REQUEST, message);
        problemDetail.setType(create("validation-error"));
        problemDetail.setTitle("Validation failed");
        return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraint(ConstraintViolationException exception) {
        ProblemDetail problemDetail = forStatusAndDetail(BAD_REQUEST, exception.getMessage());
        problemDetail.setType(create("constraint-violation"));
        problemDetail.setTitle("Constraint violation");
        return problemDetail;
    }

    @ExceptionHandler(NoSuchElementException.class)
    ProblemDetail handleNotFound(NoSuchElementException exception) {
        ProblemDetail problemDetail = forStatusAndDetail(NOT_FOUND, exception.getMessage());
        problemDetail.setType(create("not-found"));
        problemDetail.setTitle("Resource not found");
        return problemDetail;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handleProductNotFound(ProductNotFoundException exception) {
        ProblemDetail problemDetail = forStatusAndDetail(NOT_FOUND, exception.getMessage());
        problemDetail.setType(create("product-not-found"));
        problemDetail.setTitle("Product not found");
        return problemDetail;
    }

    @ExceptionHandler(FeatureNotAvailableException.class)
    ProblemDetail handleFeatureNotAvailable(FeatureNotAvailableException exception) {
        ProblemDetail problemDetail = forStatusAndDetail(NOT_FOUND, exception.getMessage());
        problemDetail.setType(create("feature-disabled"));
        problemDetail.setTitle("Feature not available");
        return problemDetail;
    }
}
