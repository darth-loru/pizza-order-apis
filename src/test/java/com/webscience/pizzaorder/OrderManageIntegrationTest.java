package com.webscience.pizzaorder;

import com.webscience.pizzaorder.model.Order;
import com.webscience.pizzaorder.model.OrderEntry;
import com.webscience.pizzaorder.model.OrderStatus;
import com.webscience.pizzaorder.repo.EntryTypeRepo;
import com.webscience.pizzaorder.repo.OrderRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class OrderManageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private EntryTypeRepo entryTypeRepo;

    @BeforeEach
    void setUp() {
        Order order1 = Order.builder()
                .id("order-id-1")
                .userName("Davide")
                .orderStatus(OrderStatus.WAITING)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(entryTypeRepo.findById("MARG").get())
                        .quantity(2)
                        .build()))
                .build();

        orderRepo.addOrder(order1);

        Order order2 = Order.builder()
                .id("order-id-2")
                .userName("Marco")
                .orderStatus(OrderStatus.WAITING)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(entryTypeRepo.findById("DIAV").get())
                        .quantity(1)
                        .build()))
                .build();

        orderRepo.addOrder(order2);

        Order order3 = Order.builder()
                .id("order-id-3")
                .userName("Matteo")
                .orderStatus(OrderStatus.WAITING)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(entryTypeRepo.findById("DIAV").get())
                        .quantity(1)
                        .build()))
                .build();

        orderRepo.addOrder(order3);
    }

    @AfterEach
    void tearDown() {
        orderRepo.clear();
    }

    @Test
    void givenMixedOrders_whenToBeProcessedOrdersRequired_thenAllOrdersAreReturned() throws Exception {
        orderRepo.setOrderCompleted(orderRepo.getOrderById("order-id-1").get());
        orderRepo.setOrderInProgress(orderRepo.getOrderById("order-id-2").get());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/manage/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].id", is(List.of("order-id-3"))));
    }

    @Test
    void givenMixedOrders_whenAllOrdersRequired_thenAllOrdersAreReturned() throws Exception {
        orderRepo.setOrderCompleted(orderRepo.getOrderById("order-id-1").get());
        orderRepo.setOrderInProgress(orderRepo.getOrderById("order-id-2").get());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/manage/order/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].id", is(List.of("order-id-1", "order-id-2", "order-id-3"))));
    }

    @Test
    void givenValidOrder_whenSetOrderInProgress_thenOrderStatusIsChanged() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-1/start"))
                        .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customer/order/order-id-1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/manage/order/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("order-id-1")))
                .andExpect(jsonPath("$.username", is("Davide")))
                .andExpect(jsonPath("$.entries[0].type", is("MARG")))
                .andExpect(jsonPath("$.entries[0].quantity", is(2)))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    @Test
    void givenNotExistingOrder_whenSetOrderInProgress_thenNotFoundIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-not-existing/start"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAnAlreadyInProgressOrder_whenSetOrderInProgress_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-1/start"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-1/start"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Cannot start an order when another one is in progress")))
                .andExpect(jsonPath("$.code", is("ORDER_ALREADY_IN_PROGRESS")));
    }

    @Test
    void givenAnotherAlreadyInProgressOrder_whenSetOrderInProgress_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-1/start"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-2/start"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Cannot start an order when another one is in progress")))
                .andExpect(jsonPath("$.code", is("ORDER_ALREADY_IN_PROGRESS")));
    }

    @Test
    void givenACompletedOrder_whenSetOrderInProgress_thenBadRequestIsReturned() throws Exception {
        orderRepo.setOrderCompleted(orderRepo.getOrderById("order-id-1").get());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-1/start"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Order cannot be started because already processed")))
                .andExpect(jsonPath("$.code", is("ORDER_ALREADY_PROCESSED")));
    }

    @Test
    void givenValidOrder_whenSetOrderCompleted_thenOrderStatusIsChanged() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customer/order/order-id-1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("WAITING")));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-1/start"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customer/order/order-id-1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-1/completed"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customer/order/order-id-1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/manage/order/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void givenNoInProgressOrder_whenSetOrderCompleted_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-1/completed"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Order is not in progress")))
                .andExpect(jsonPath("$.code", is("ORDER_NOT_IN_PROGRESS")));
    }


    @Test
    void givenAnotherInProgressOrder_whenSetOrderCompleted_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-1/start"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/manage/order/order-id-2/completed"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Order is not in progress")))
                .andExpect(jsonPath("$.code", is("ORDER_NOT_IN_PROGRESS")));
    }
}
