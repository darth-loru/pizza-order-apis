package com.webscience.pizzaorder.service;

import com.webscience.pizzaorder.dto.OrderCreationRequestDTO;
import com.webscience.pizzaorder.dto.OrderDetailsResponseDTO;
import com.webscience.pizzaorder.dto.OrderEntryDTO;
import com.webscience.pizzaorder.dto.OrderStatusResponseDTO;
import com.webscience.pizzaorder.exception.*;
import com.webscience.pizzaorder.model.EntryType;
import com.webscience.pizzaorder.model.Order;
import com.webscience.pizzaorder.model.OrderEntry;
import com.webscience.pizzaorder.model.OrderStatus;
import com.webscience.pizzaorder.repo.EntryTypeRepo;
import com.webscience.pizzaorder.repo.OrderRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    private static final EntryType MARGHERITA_TYPE = EntryType.builder()
            .id("MARG")
            .description("Margherita")
            .build();

    private static final EntryType DIAVOLA_TYPE = EntryType.builder()
            .id("DIAV")
            .description("Diavola")
            .build();

    private static final OffsetDateTime FIXED_TS = LocalDate.of(2023, 9, 11)
            .atStartOfDay()
            .atOffset(ZoneOffset.UTC);

    @Mock
    private EntryTypeRepo entryTypeRepo;

    @Mock
    private OrderRepo orderRepo;

    private final Clock clock = Clock.fixed(FIXED_TS.toInstant(), ZoneId.of("UTC"));

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(entryTypeRepo, orderRepo, clock);

        lenient().when(entryTypeRepo.findById("MARG")).thenReturn(Optional.of(MARGHERITA_TYPE));
        lenient().when(entryTypeRepo.findById("DIAV")).thenReturn(Optional.of(DIAVOLA_TYPE));

        lenient().when(orderRepo.generate()).thenCallRealMethod();
    }

    @Test
    void givenAValidOrder_whenCreatingOrder_theOrderIsCorrectlyCreated() {
        OrderCreationRequestDTO requestDTO = OrderCreationRequestDTO.builder()
                .username("Davide")
                .entries(List.of(
                        OrderEntryDTO.builder()
                                .type("MARG")
                                .quantity(1)
                                .build()
                ))
                .build();

        String orderId = orderService.createOrder(requestDTO);

        assertNotNull(orderId);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        verify(orderRepo).addOrder(orderCaptor.capture());

        Order order = orderCaptor.getValue();
        assertEquals(OrderStatus.WAITING, order.getOrderStatus());
        assertEquals("Davide", order.getUserName());
        assertEquals(orderId, order.getId());
        assertEquals(FIXED_TS, order.getInsertTs());

        List<OrderEntry> expectedEntries = List.of(OrderEntry.builder()
                .entryType(MARGHERITA_TYPE)
                .quantity(1)
                .build());

        assertEquals(expectedEntries, order.getOrderEntries());
    }

    @Test
    void givenAValidOrderWithMultipleEntries_whenCreatingOrder_theOrderIsCorrectlyCreated() {
        OrderCreationRequestDTO requestDTO = OrderCreationRequestDTO.builder()
                .username("Davide")
                .entries(List.of(
                        OrderEntryDTO.builder()
                                .type("MARG")
                                .quantity(1)
                                .build(),
                        OrderEntryDTO.builder()
                                .type("DIAV")
                                .quantity(2)
                                .build()
                ))
                .build();

        String orderId = orderService.createOrder(requestDTO);

        assertNotNull(orderId);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        verify(orderRepo).addOrder(orderCaptor.capture());

        Order order = orderCaptor.getValue();
        assertEquals(OrderStatus.WAITING, order.getOrderStatus());
        assertEquals("Davide", order.getUserName());
        assertEquals(orderId, order.getId());
        assertEquals(FIXED_TS, order.getInsertTs());

        List<OrderEntry> expectedEntries = List.of(
                OrderEntry.builder()
                        .entryType(MARGHERITA_TYPE)
                        .quantity(1)
                        .build(),
                OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(2)
                        .build());

        assertEquals(expectedEntries, order.getOrderEntries());
    }

    @Test
    void givenANotValidEntryType_whenCreatingOrder_theExceptionIsThrown() {
        OrderCreationRequestDTO requestDTO = OrderCreationRequestDTO.builder()
                .username("Davide")
                .entries(List.of(
                        OrderEntryDTO.builder()
                                .type("MARG")
                                .quantity(1)
                                .build(),
                        OrderEntryDTO.builder()
                                .type("UNKNOWN")
                                .quantity(2)
                                .build()
                ))
                .build();

        assertThrows(InvalidEntryTypeException.class, () -> orderService.createOrder(requestDTO));
    }

    @Test
    void givenAnExistingOrder_whenRequestingStatus_thenStatusIsReturned() {
        Order order = Order.builder()
                .id("an-order-id")
                .userName("Davide")
                .orderStatus(OrderStatus.IN_PROGRESS)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(2)
                        .build()))
                .build();

        when(orderRepo.getOrderById("an-order-id")).thenReturn(Optional.of(order));

        OrderStatusResponseDTO orderStatus = orderService.getOrderStatus("an-order-id");
        assertEquals(OrderStatus.IN_PROGRESS, orderStatus.getStatus());
    }

    @Test
    void givenANotExistingOrder_whenRequestingStatus_thenExceptionIsThrown() {
        Order order = Order.builder()
                .id("an-order-id")
                .userName("Davide")
                .orderStatus(OrderStatus.IN_PROGRESS)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(2)
                        .build()))
                .build();

        lenient().when(orderRepo.getOrderById("an-order-id")).thenReturn(Optional.of(order));

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderStatus("not-existing-order"));
    }

    @Test
    void givenAnExistingOrder_whenRequestingDetails_thenStatusIsReturned() {
        Order order = Order.builder()
                .id("an-order-id")
                .userName("Davide")
                .orderStatus(OrderStatus.IN_PROGRESS)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(2)
                        .build()))
                .build();

        when(orderRepo.getOrderById("an-order-id")).thenReturn(Optional.of(order));

        OrderDetailsResponseDTO orderDetails = orderService.getOrderDetails("an-order-id");
        assertEquals("an-order-id", orderDetails.getId());
        assertEquals("Davide", orderDetails.getUsername());
        assertEquals(OrderStatus.IN_PROGRESS, orderDetails.getStatus());

        List<OrderEntryDTO> expectedEntries = List.of(OrderEntryDTO.builder()
                .type("DIAV")
                .quantity(2)
                .build());
        assertEquals(expectedEntries, orderDetails.getEntries());
    }

    @Test
    void givenANotExistingOrder_whenRequestingDetails_thenExceptionIsThrown() {
        Order order = Order.builder()
                .id("an-order-id")
                .userName("Davide")
                .orderStatus(OrderStatus.IN_PROGRESS)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(2)
                        .build()))
                .build();

        lenient().when(orderRepo.getOrderById("an-order-id")).thenReturn(Optional.of(order));

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderDetails("not-existing-order"));
    }

    @Test
    void givenNoOrdersInProgressAndValidOrder_whenStartingProgress_orderStatusIsChanged() {
        Order order = Order.builder()
                .id("an-order-id")
                .userName("Davide")
                .orderStatus(OrderStatus.WAITING)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(2)
                        .build()))
                .build();

        lenient().when(orderRepo.getOrderById("an-order-id")).thenReturn(Optional.of(order));

        orderService.startProcessingOrder("an-order-id");

        verify(orderRepo).setOrderInProgress(order);
    }

    @Test
    void givenSameOrderAlreadyInProgress_whenStartingProgress_orderExceptionIsThrown() {
        when(orderRepo.getOrderInProgress()).thenReturn("an-order-id");
        assertThrows(OrderAlreadyInProgressException.class, () -> orderService.startProcessingOrder("an-order-id"));
    }

    @Test
    void givenADifferentOrderAlreadyInProgress_whenStartingProgress_orderExceptionIsThrown() {
        when(orderRepo.getOrderInProgress()).thenReturn("another-order-id");
        assertThrows(OrderAlreadyInProgressException.class, () -> orderService.startProcessingOrder("an-order-id"));
    }

    @Test
    void givenANotExistingOrder_whenStartingProgress_orderExceptionIsThrown() {
        Order order = Order.builder()
                .id("an-order-id")
                .userName("Davide")
                .orderStatus(OrderStatus.WAITING)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(2)
                        .build()))
                .build();

        lenient().when(orderRepo.getOrderById("an-order-id")).thenReturn(Optional.of(order));

        assertThrows(OrderNotFoundException.class, () -> orderService.startProcessingOrder("not-existing-order"));
    }

    @Test
    void givenAnAlreadyProcessedOrder_whenStartingProgress_orderExceptionIsThrown() {
        Order order = Order.builder()
                .id("an-order-id")
                .userName("Davide")
                .orderStatus(OrderStatus.COMPLETED)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(2)
                        .build()))
                .build();

        lenient().when(orderRepo.getOrderById("an-order-id")).thenReturn(Optional.of(order));

        assertThrows(OrderAlreadyProcessedException.class, () -> orderService.startProcessingOrder("an-order-id"));
    }

    @Test
    void givenAnOrderInProgress_whenSettingCompleted_orderStatusIsChanged() {
        Order order = Order.builder()
                .id("an-order-id")
                .userName("Davide")
                .orderStatus(OrderStatus.IN_PROGRESS)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(2)
                        .build()))
                .build();

        lenient().when(orderRepo.getOrderById("an-order-id")).thenReturn(Optional.of(order));

        when(orderRepo.getOrderInProgress()).thenReturn("an-order-id");

        orderService.setOrderCompleted("an-order-id");

        verify(orderRepo).setOrderCompleted(order);
    }

    @Test
    void givenNoInProgress_whenSettingCompleted_orderExceptionIsThrown() {
        assertThrows(OrderNotInProgressException.class, () -> orderService.setOrderCompleted("an-order-id"));
    }

    @Test
    void givenADifferentOrderIsInProgress_whenSettingCompleted_orderExceptionIsThrown() {
        when(orderRepo.getOrderInProgress()).thenReturn("another-order-id");
        assertThrows(OrderNotInProgressException.class, () -> orderService.setOrderCompleted("an-order-id"));
    }

    @Test
    void givenAnOrderInProgress_whenGettingOrderInProgress_orderIsReturned() {
        Order order = Order.builder()
                .id("an-order-id")
                .userName("Davide")
                .orderStatus(OrderStatus.IN_PROGRESS)
                .orderEntries(List.of(OrderEntry.builder()
                        .entryType(DIAVOLA_TYPE)
                        .quantity(2)
                        .build()))
                .build();

        lenient().when(orderRepo.getOrderById("an-order-id")).thenReturn(Optional.of(order));

        when(orderRepo.getOrderInProgress()).thenReturn("an-order-id");

        OrderDetailsResponseDTO orderInProgressDetails = orderService.getOrderInProgress();

        assertEquals("an-order-id", orderInProgressDetails.getId());
        assertEquals("Davide", orderInProgressDetails.getUsername());
        assertEquals(OrderStatus.IN_PROGRESS, orderInProgressDetails.getStatus());

        List<OrderEntryDTO> expectedEntries = List.of(OrderEntryDTO.builder()
                .type("DIAV")
                .quantity(2)
                .build());
        assertEquals(expectedEntries, orderInProgressDetails.getEntries());
    }

    @Test
    void givenNoOrderInProgress_whenGettingOrderInProgress_nullIsReturned() {
        assertNull(orderService.getOrderInProgress());
    }
}