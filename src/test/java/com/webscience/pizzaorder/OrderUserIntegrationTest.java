package com.webscience.pizzaorder;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class OrderUserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenAValidOrderRequest_whenRequestIsSent_thenOrderIsCreated() throws Exception {
        MvcResult creationResults = mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "Davide",
                                    "entries": [
                                        {
                                            "type": "MARG",
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA",
                                            "quantity": 2
                                        }
                                    ]
                                }"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andReturn();

        String id = JsonPath.read(creationResults.getResponse().getContentAsString(), "$.orderId");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customer/order/" + id + "/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.username", is("Davide")))
                .andExpect(jsonPath("$.entries[0].type", is("MARG")))
                .andExpect(jsonPath("$.entries[0].quantity", is(1)))
                .andExpect(jsonPath("$.entries[1].type", is("BUFA")))
                .andExpect(jsonPath("$.entries[1].quantity", is(2)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void givenAnOrderRequestWithAdditionalIngredients_whenRequestIsSent_thenOrderIsCreated() throws Exception {
        MvcResult creationResults = mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "Davide",
                                    "entries": [
                                        {
                                            "type": "MARG",
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA",
                                            "quantity": 2,
                                            "additionalIngredients": [
                                                "prosciutto cotto",
                                                "olive"
                                            ]
                                        }
                                    ]
                                }"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andReturn();

        String id = JsonPath.read(creationResults.getResponse().getContentAsString(), "$.orderId");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customer/order/" + id + "/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.username", is("Davide")))
                .andExpect(jsonPath("$.entries[0].type", is("MARG")))
                .andExpect(jsonPath("$.entries[0].quantity", is(1)))
                .andExpect(jsonPath("$.entries[1].type", is("BUFA")))
                .andExpect(jsonPath("$.entries[1].quantity", is(2)))
                .andExpect(jsonPath("$.entries[1].additionalIngredients", is(List.of("prosciutto cotto", "olive"))))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void givenANotExistingOrder_whenGettingOrderDetails_thenNotFoundIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customer/order/not-existing-id/details"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenANotExistingOrder_whenGettingOrderStatus_thenNotFoundIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customer/order/not-existing-id/status"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAValidOrderRequest_whenRequestIsSent_thenOrderStatusIsWaiting() throws Exception {
        MvcResult creationResults = mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "Davide",
                                    "entries": [
                                        {
                                            "type": "MARG",
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA",
                                            "quantity": 2
                                        }
                                    ]
                                }"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andReturn();

        String id = JsonPath.read(creationResults.getResponse().getContentAsString(), "$.orderId");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customer/order/" + id + "/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void givenAnOrderRequestWithoutUsername_whenRequestIsSent_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "entries": [
                                        {
                                            "type": "MARG",
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA",
                                            "quantity": 2
                                        }
                                    ]
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andReturn();
    }

    @Test
    void givenAnOrderRequestWithEmptyUsername_whenRequestIsSent_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "",
                                    "entries": [
                                        {
                                            "type": "MARG",
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA",
                                            "quantity": 2
                                        }
                                    ]
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andReturn();
    }

    @Test
    void givenAnOrderRequestWithoutEntries_whenRequestIsSent_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "Davide",
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Failed to read request")))
                .andReturn();
    }

    @Test
    void givenAnOrderRequestWithEmptyEntries_whenRequestIsSent_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "",
                                    "entries": []
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andReturn();
    }

    @Test
    void givenAnOrderRequestWithNullEntryType_whenRequestIsSent_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "Davide",
                                    "entries": [
                                        {
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA",
                                            "quantity": 2
                                        }
                                    ]
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andReturn();
    }

    @Test
    void givenAnOrderRequestWithEmptyEntryType_whenRequestIsSent_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "Davide",
                                    "entries": [
                                        {
                                            "type": "",
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA",
                                            "quantity": 2
                                        }
                                    ]
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andReturn();
    }

    @Test
    void givenAnOrderRequestWithInvalidEntryType_whenRequestIsSent_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "Davide",
                                    "entries": [
                                        {
                                            "type": "UNKNOWN",
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA",
                                            "quantity": 2
                                        }
                                    ]
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid entry type")))
                .andExpect(jsonPath("$.code", is("INVALID_ENTRY_TYPE")))
                .andReturn();
    }

    @Test
    void givenAnOrderRequestWithoutQuantity_whenRequestIsSent_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "Davide",
                                    "entries": [
                                        {
                                            "type": "MARG",
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA"
                                        }
                                    ]
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andReturn();
    }

    @Test
    void givenAnOrderRequestWithNegativeQuantity_whenRequestIsSent_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "Davide",
                                    "entries": [
                                        {
                                            "type": "MARG",
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA",
                                            "quantity": -3
                                        }
                                    ]
                                }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andReturn();
    }

    @Test
    void givenAnOrderRequestWithZeroQuantity_whenRequestIsSent_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customer/order")
                        .contentType("application/json")
                        .content("""
                                {
                                    "username": "Davide",
                                    "entries": [
                                        {
                                            "type": "MARG",
                                            "quantity": 1
                                        },
                                        {
                                            "type": "BUFA",
                                            "quantity": 0
                                        }
                                    ]
                                }"""))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
