package com.webscience.pizzaorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "An order is already in progress")
public class OrderAlreadyInProgressException extends RuntimeException {
}
