package com.webscience.pizzaorder.controller;

import com.webscience.pizzaorder.dto.OrderDetailsResponseDTO;
import com.webscience.pizzaorder.service.OrderService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "manage_orders", description = "Manage Orders")
@RestController
@RequestMapping("/api/manage/order")
@Validated
@RequiredArgsConstructor
public class ManagerOrderController {

    private final OrderService orderService;

    @Operation(summary = "Get orders to be processed")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDetailsResponseDTO> getOrdersToBeProcessed() {
        return orderService.getOrdersToBeProcessed();
    }

    @Operation(summary = "Get ALL orders")
    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDetailsResponseDTO> getAllOrders() {
        return orderService.getAllOrderDetails();
    }

    @Operation(summary = "Take the next order (set in progress)")
    @PutMapping(value = "{orderId}/start", produces = MediaType.APPLICATION_JSON_VALUE)
    public void startProcessing(@Parameter(name = "Order Id", required = true) @PathVariable String orderId) {
        orderService.startProcessingOrder(orderId);
    }

    @Operation(summary = "Mark the current order in progress as completed")
    @PutMapping(value = "{orderId}/completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public void completeProcessing(@Parameter(name = "Order Id", required = true) @PathVariable String orderId) {
        orderService.setOrderCompleted(orderId);
    }

    @Operation(summary = "Get the current order in progress")
    @GetMapping(value = "current", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderDetailsResponseDTO getOrderInProgress() {
        return orderService.getOrderInProgress();
    }

    @Operation(summary = "Get order details")
    @GetMapping(value = "{orderId}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderDetailsResponseDTO getDetails(@Parameter(name = "Order Id", required = true) @PathVariable String orderId) {
        return orderService.getOrderDetails(orderId);
    }
}
