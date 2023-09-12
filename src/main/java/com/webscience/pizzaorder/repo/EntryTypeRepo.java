package com.webscience.pizzaorder.repo;

import com.webscience.pizzaorder.model.EntryType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EntryTypeRepo {
    List<EntryType> availableTypes = List.of(
            EntryType.builder()
                    .id("MARG")
                    .description("Margherita")
                    .ingredients(List.of(
                            "Pomodoro",
                            "Mozzarella",
                            "Basilico"
                    ))
                    .build(),
            EntryType.builder()
                    .id("BUFA")
                    .description("Bufalina")
                    .ingredients(List.of(
                            "Pomodoro",
                            "Pomodorini freschi",
                            "Mozzarella di Bufala"
                    ))
                    .build(),
            EntryType.builder()
                    .id("DIAV")
                    .description("Diavola")
                    .ingredients(List.of(
                            "Pomodoro",
                            "Mozzarella",
                            "Salame piccante"
                    ))
                    .build(),
            EntryType.builder()
                    .id("WURS")
                    .description("Wurstel")
                    .ingredients(List.of(
                            "Pomodoro",
                            "Mozzarella",
                            "Wurstel"
                    ))
                    .build()
    );

    public Optional<EntryType> findById(String typeId) {
        if (typeId == null) {
            return Optional.empty();
        }

        return availableTypes.stream()
                .filter(type -> typeId.equals(type.id()))
                .findFirst();
    }
}
