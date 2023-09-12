package com.webscience.pizzaorder.controller;

import com.webscience.pizzaorder.dto.OrderCreationRequestDTO;
import com.webscience.pizzaorder.dto.OrderCreationResponseDTO;
import com.webscience.pizzaorder.dto.OrderDetailsResponseDTO;
import com.webscience.pizzaorder.dto.OrderStatusResponseDTO;
import com.webscience.pizzaorder.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "user_order", description = "Users orders")
@RestController
@RequestMapping("/api/customer/order")
@Validated
@RequiredArgsConstructor
public class CustomerOrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderCreationResponseDTO createOrder(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Order creation request") @RequestBody @Valid OrderCreationRequestDTO orderCreationRequestDTO) {
        String orderId = orderService.createOrder(orderCreationRequestDTO);

        return OrderCreationResponseDTO.builder()
                .orderId(orderId)
                .build();
    }

    @Operation(summary = "Get order status")
    @GetMapping(value = "{orderId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderStatusResponseDTO getStatus(@Parameter(name = "Order Id", required = true) @PathVariable String orderId) {
        return orderService.getOrderStatus(orderId);
    }

    @Operation(summary = "Get order details")
    @GetMapping(value = "{orderId}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderDetailsResponseDTO getDetails(@Parameter(name = "Order Id", required = true) @PathVariable String orderId) {
        return orderService.getOrderDetails(orderId);
    }
}
