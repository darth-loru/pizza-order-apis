package com.webscience.pizzaorder.repo;

import com.webscience.pizzaorder.model.EntryType;
import com.webscience.pizzaorder.model.Order;
import com.webscience.pizzaorder.model.OrderEntry;
import com.webscience.pizzaorder.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepoUnitTest {

    private static final EntryType MARGHERITA_TYPE = EntryType.builder()
            .id("MARG")
            .description("Margherita")
            .build();

    private static final EntryType DIAVOLA_TYPE = EntryType.builder()
            .id("DIAV")
            .description("Diavola")
            .build();

    private static final Order EXISTING_ORDER_1 = Order.builder()
            .id("an-order-id")
            .userName("Davide")
            .orderStatus(OrderStatus.WAITING)
            .orderEntries(List.of(OrderEntry.builder()
                    .entryType(MARGHERITA_TYPE)
                    .quantity(2)
                    .build()))
            .build();

    private final Order EXISTING_ORDER_2 = Order.builder()
            .id("another-order-id")
            .userName("Marco")
            .orderStatus(OrderStatus.WAITING)
            .orderEntries(List.of(OrderEntry.builder()
                    .entryType(DIAVOLA_TYPE)
                    .quantity(1)
                    .build()))
            .build();

    private OrderRepo orderRepo;

    @BeforeEach
    void setUp() {
        orderRepo = new OrderRepo();

        orderRepo.addOrder(EXISTING_ORDER_1);
        orderRepo.addOrder(EXISTING_ORDER_2);
    }

    @Test
    void whenGeneratingId_thenANotNullIdIsReturned() {
        assertNotNull(orderRepo.generate());
    }

    @Test
    void givenMultipleOrdersAdded_whenAddingOrder_thenOrderSortingIsPreserved() {
        Order newOrder = Order.builder()
                .id("new-order-id")
                .userName("Marco")
                .orderStatus(OrderStatus.WAITING)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(1)
                        .build()))
                .build();

        orderRepo.addOrder(newOrder);

        assertEquals(List.of(EXISTING_ORDER_1, EXISTING_ORDER_2, newOrder), orderRepo.getAll());
    }

    @Test
    void givenAValidOrder_whenGettingById_thenOrderIsRetrieved() {
        assertEquals(Optional.of(EXISTING_ORDER_1), orderRepo.getOrderById("an-order-id"));
    }

    @Test
    void givenANotValidOrder_whenGettingById_thenEmptyIsReturned() {
        assertEquals(Optional.empty(), orderRepo.getOrderById("not-existing-order"));
    }

    @Test
    void givenAValidOrder_whenSettingInProgress_thenOrderStatusIsChanged() {
        Order newOrder = Order.builder()
                .id("new-order-id")
                .userName("Marco")
                .orderStatus(OrderStatus.WAITING)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(1)
                        .build()))
                .build();

        orderRepo.addOrder(newOrder);

        assertNull(orderRepo.getOrderInProgress());

        orderRepo.setOrderInProgress(newOrder);
        assertEquals(OrderStatus.IN_PROGRESS, newOrder.getOrderStatus());
        assertEquals(orderRepo.getOrderInProgress(), "new-order-id");
    }

    @Test
    void givenAnOrderInProgress_whenSettingCompleted_thenOrderStatusIsChanged() {
        Order newOrder = Order.builder()
                .id("new-order-id")
                .userName("Marco")
                .orderStatus(OrderStatus.WAITING)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(1)
                        .build()))
                .build();

        orderRepo.addOrder(newOrder);

        orderRepo.setOrderInProgress(newOrder);
        assertEquals(OrderStatus.IN_PROGRESS, newOrder.getOrderStatus());
        assertEquals(orderRepo.getOrderInProgress(), "new-order-id");

        orderRepo.setOrderCompleted(newOrder);
        assertEquals(OrderStatus.COMPLETED, newOrder.getOrderStatus());
        assertNull(orderRepo.getOrderInProgress());

    }

    @Test
    void givenMultipleOrdersWithDifferentStatuses_whenGettingAllOrders_thenAllOrdersAreReturned() {
        Order completedOrder = Order.builder()
                .id("completed-order-id")
                .userName("Marco")
                .orderStatus(OrderStatus.COMPLETED)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(1)
                        .build()))
                .build();

        orderRepo.addOrder(completedOrder);

        Order inProgressOrder = Order.builder()
                .id("completed-order-id")
                .userName("Marco")
                .orderStatus(OrderStatus.IN_PROGRESS)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(1)
                        .build()))
                .build();

        orderRepo.addOrder(inProgressOrder);

        List<Order> expectedOrders = List.of(EXISTING_ORDER_1, EXISTING_ORDER_2, completedOrder, inProgressOrder);
        assertEquals(expectedOrders, orderRepo.getAll());
    }

    @Test
    void givenMultipleOrdersWithDifferentStatuses_whenGettingOrdersToBeProcessed_thenOnlyWaitingOrdersAreReturned() {
        Order completedOrder = Order.builder()
                .id("completed-order-id")
                .userName("Marco")
                .orderStatus(OrderStatus.COMPLETED)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(1)
                        .build()))
                .build();

        orderRepo.addOrder(completedOrder);

        Order inProgressOrder = Order.builder()
                .id("completed-order-id")
                .userName("Marco")
                .orderStatus(OrderStatus.IN_PROGRESS)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(1)
                        .build()))
                .build();

        orderRepo.addOrder(inProgressOrder);

        List<Order> expectedOrders = List.of(EXISTING_ORDER_1, EXISTING_ORDER_2);
        assertEquals(expectedOrders, orderRepo.getNotProcessed());
    }
}