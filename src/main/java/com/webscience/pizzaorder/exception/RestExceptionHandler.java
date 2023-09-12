package com.webscience.pizzaorder.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidEntryTypeException.class)
    protected ResponseEntity<Object> handleException(InvalidEntryTypeException ex) {
        return buildResponseEntity(new RestApiError(HttpStatus.BAD_REQUEST, "INVALID_ENTRY_TYPE", "Invalid entry type"));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    protected ResponseEntity<Object> handleException(OrderNotFoundException ex) {
        return buildResponseEntity(new RestApiError(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order id not found"));
    }

    @ExceptionHandler(OrderNotInProgressException.class)
    protected ResponseEntity<Object> handleException(OrderNotInProgressException ex) {
        return buildResponseEntity(new RestApiError(HttpStatus.BAD_REQUEST, "ORDER_NOT_IN_PROGRESS", "Order is not in progress"));
    }

    @ExceptionHandler(OrderAlreadyInProgressException.class)
    protected ResponseEntity<Object> handleException(OrderAlreadyInProgressException ex) {
        return buildResponseEntity(new RestApiError(HttpStatus.BAD_REQUEST, "ORDER_ALREADY_IN_PROGRESS", "Cannot start an order when another one is in progress"));
    }

    @ExceptionHandler(OrderAlreadyProcessedException.class)
    protected ResponseEntity<Object> handleException(OrderAlreadyProcessedException ex) {
        log.warn("Invalid order entry type", ex);
        return buildResponseEntity(new RestApiError(HttpStatus.BAD_REQUEST, "ORDER_ALREADY_PROCESSED", "Order cannot be started because already processed"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleItemNotDeletable(ConstraintViolationException ex) {
        return buildResponseEntity(new RestApiError(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", ex.getMessage()));
    }

    private ResponseEntity<Object> buildResponseEntity(RestApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
