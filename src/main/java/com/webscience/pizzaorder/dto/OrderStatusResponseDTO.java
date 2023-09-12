package com.webscience.pizzaorder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webscience.pizzaorder.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "order_status_response", description = "Order status response")
@Getter
@Builder
public class OrderStatusResponseDTO {

    @Schema(description = "Status of the order", example = "WAITING", allowableValues = "WAITING, IN_PROGRESS, COMPLETED")
    @JsonProperty
    private final OrderStatus status;
}
