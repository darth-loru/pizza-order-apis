package com.webscience.pizzaorder.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Schema(name = "order_entry", description = "An entry is a type of pizza with quantities and, optionally, additional ingredients")
@EqualsAndHashCode
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderEntryDTO {

    @Schema(description = "Code of the type of entry", example = "MARG", required = true)
    @NotEmpty
    @JsonProperty
    private final String type;

    @Schema(description = "Quantity", example = "2", required = true)
    @Positive
    @JsonProperty
    private final int quantity;

    @Schema(description = "Additional ingredients", example = "[ \"patatine\", \"olive\"]")
    @JsonProperty
    private final List<String> additionalIngredients;
}
