package com.webscience.pizzaorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Order invalid or not found")
public class OrderNotFoundException extends RuntimeException {
}
