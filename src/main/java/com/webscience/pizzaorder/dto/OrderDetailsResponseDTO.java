package com.webscience.pizzaorder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webscience.pizzaorder.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(name = "order_details", description = "Order creation response")
@Getter
@Builder
public class OrderDetailsResponseDTO {

    @Schema(description = "Order id", example = "9e40b6ef-9eed-4dfa-a0fc-e90409b64e39")
    @JsonProperty
    private final String id;

    @Schema(description = "Name of the user", example = "Davide")
    @JsonProperty
    private final String username;

    @Schema(description = "List of order entries")
    @JsonProperty
    private final List<OrderEntryDTO> entries;

    @Schema(description = "Status of the order", example = "WAITING", allowableValues = "WAITING, IN_PROGRESS, COMPLETED")
    @JsonProperty
    private final OrderStatus status;
}
