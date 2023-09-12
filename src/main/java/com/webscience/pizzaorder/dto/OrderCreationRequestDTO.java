package com.webscience.pizzaorder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(name = "order_creation_request", description = "Order creation request")
@Getter
@Builder
public class OrderCreationRequestDTO {

    @Schema(description = "Name of the user", example = "Davide")
    @NotEmpty(message = "User name cannot be empty.")
    @JsonProperty
    private final String username;

    @Schema(description = "List of order entries")
    @NotEmpty(message = "Order entries list cannot be empty.")
    @JsonProperty
    private final List<@Valid OrderEntryDTO> entries;
}
