package com.webscience.pizzaorder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid order entry type")
public class InvalidEntryTypeException extends RuntimeException {
}
