package com.webscience.pizzaorder.repo;

import com.webscience.pizzaorder.model.EntryType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EntryTypeRepoUnitTest {

    private final EntryTypeRepo entryTypeRepo = new EntryTypeRepo();

    @Test
    void givenAValidTypeId_whenGettingType_thenTypeIsReturned() {
        EntryType expectedEntryType = EntryType.builder()
                .id("MARG")
                .description("Margherita")
                .ingredients(List.of(
                        "Pomodoro",
                        "Mozzarella",
                        "Basilico"
                ))
                .build();

        assertEquals(Optional.of(expectedEntryType), entryTypeRepo.findById("MARG"));
    }

    @Test
    void givenAnInvalidTypeId_whenGettingType_thenTypeIsReturned() {
        assertEquals(Optional.empty(), entryTypeRepo.findById("UNKNOWN"));
    }
}