package com.webscience.pizzaorder.service;

import com.webscience.pizzaorder.dto.OrderDetailsResponseDTO;
import com.webscience.pizzaorder.dto.OrderEntryDTO;
import com.webscience.pizzaorder.dto.OrderCreationRequestDTO;
import com.webscience.pizzaorder.dto.OrderStatusResponseDTO;
import com.webscience.pizzaorder.exception.*;
import com.webscience.pizzaorder.model.EntryType;
import com.webscience.pizzaorder.model.Order;
import com.webscience.pizzaorder.model.OrderEntry;
import com.webscience.pizzaorder.model.OrderStatus;
import com.webscience.pizzaorder.repo.EntryTypeRepo;
import com.webscience.pizzaorder.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final EntryTypeRepo entryTypeRepo;
    private final OrderRepo orderRepo;
    private final Clock clock;

    private final Object lock = new Object();

    public String createOrder(OrderCreationRequestDTO requestDTO) {
        String orderId = orderRepo.generate();
        Order order = buildOrderModel(orderId, requestDTO);
        orderRepo.addOrder(order);
        return orderId;
    }

    private Order buildOrderModel(String orderId, OrderCreationRequestDTO requestDTO) {
        List<OrderEntry> orderEntries = requestDTO.getEntries().stream()
                .map(this::buildOrderItemModel)
                .collect(Collectors.toList());

        return Order.builder()
                .id(orderId)
                .userName(requestDTO.getUsername())
                .orderEntries(orderEntries)
                .orderStatus(OrderStatus.WAITING)
                .insertTs(OffsetDateTime.now(clock))
                .build();
    }

    private OrderEntry buildOrderItemModel(OrderEntryDTO orderEntryDTO) {
        EntryType type = entryTypeRepo.findById(orderEntryDTO.getType())
                .orElseThrow(InvalidEntryTypeException::new);

        return OrderEntry.builder()
                .entryType(type)
                .quantity(orderEntryDTO.getQuantity())
                .additionalIngredients(orderEntryDTO.getAdditionalIngredients())
                .build();
    }

    public OrderStatusResponseDTO getOrderStatus(String orderId) {
        return orderRepo.getOrderById(orderId)
                .map(this::buildOrderStatus)
                .orElseThrow(OrderNotFoundException::new);
    }

    private OrderStatusResponseDTO buildOrderStatus(Order order) {
        return OrderStatusResponseDTO.builder()
                .status(order.getOrderStatus())
                .build();
    }

    public OrderDetailsResponseDTO getOrderDetails(String orderId) {
        return orderRepo.getOrderById(orderId)
                .map(this::buildOrderDetails)
                .orElseThrow(OrderNotFoundException::new);
    }

    private OrderDetailsResponseDTO buildOrderDetails(Order order) {
        List<OrderEntryDTO> orderItems = order.getOrderEntries().stream()
                .map(entry -> OrderEntryDTO.builder()
                        .type(entry.entryType().id())
                        .quantity(entry.quantity())
                        .additionalIngredients(entry.additionalIngredients())
                        .build())
                .collect(Collectors.toList());

        return OrderDetailsResponseDTO.builder()
                .id(order.getId())
                .username(order.getUserName())
                .entries(orderItems)
                .status(order.getOrderStatus())
                .build();
    }

    public List<OrderDetailsResponseDTO> getAllOrderDetails() {
        return orderRepo.getAll().stream()
                .map(this::buildOrderDetails)
                .collect(Collectors.toList());
    }

    public List<OrderDetailsResponseDTO> getOrdersToBeProcessed() {
        return orderRepo.getNotProcessed().stream()
                .map(this::buildOrderDetails)
                .collect(Collectors.toList());
    }

    public void startProcessingOrder(String orderId) {
        synchronized (lock) {
            if (orderRepo.getOrderInProgress() != null) {
                throw new OrderAlreadyInProgressException();
            }

            Order order = orderRepo.getOrderById(orderId)
                    .orElseThrow(OrderNotFoundException::new);

            if (order.getOrderStatus() != OrderStatus.WAITING) {
                throw new OrderAlreadyProcessedException();
            }

            orderRepo.setOrderInProgress(order);
        }
    }

    public void setOrderCompleted(String orderId) {
        synchronized (lock) {
            String orderIdInProgress = orderRepo.getOrderInProgress();

            if (orderIdInProgress == null || !orderIdInProgress.equals(orderId)) {
                throw new OrderNotInProgressException();
            }

            Order order = orderRepo.getOrderById(orderId)
                    .orElseThrow(OrderNotFoundException::new);

            orderRepo.setOrderCompleted(order);
        }
    }

    public OrderDetailsResponseDTO getOrderInProgress() {
        String orderIdInProgress = orderRepo.getOrderInProgress();

        if (orderIdInProgress == null) {
            return null;
        }

        return orderRepo.getOrderById(orderIdInProgress)
                .map(this::buildOrderDetails)
                .orElse(null);
    }
}
