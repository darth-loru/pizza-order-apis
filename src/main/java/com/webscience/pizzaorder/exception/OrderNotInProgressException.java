package com.webscience.pizzaorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Order cannot be closed because it is not in progress")
public class OrderNotInProgressException extends RuntimeException {
}
