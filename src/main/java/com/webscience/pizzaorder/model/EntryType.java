package com.webscience.pizzaorder.model;

import lombok.Builder;

import java.util.List;

public record EntryType(String id, String description,
                        List<String> ingredients) {

    @Builder public EntryType {}
}
