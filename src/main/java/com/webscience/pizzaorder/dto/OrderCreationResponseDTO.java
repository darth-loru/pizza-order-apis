package com.webscience.pizzaorder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "order_creation_response", description = "Order creation response")
@Builder
public class OrderCreationResponseDTO {

    @Schema(description = "Order Id", example = "9e40b6ef-9eed-4dfa-a0fc-e90409b64e39")
    @JsonProperty
    private final String orderId;
}
