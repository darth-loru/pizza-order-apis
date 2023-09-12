package com.webscience.pizzaorder.model;

import lombok.Builder;

import java.util.List;

public record OrderEntry(EntryType entryType, int quantity, List<String> additionalIngredients) {
    @Builder public OrderEntry {}
}
