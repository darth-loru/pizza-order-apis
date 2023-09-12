package com.webscience.pizzaorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The order is already processed")
public class OrderAlreadyProcessedException extends RuntimeException {
}
