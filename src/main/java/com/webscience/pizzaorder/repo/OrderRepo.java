package com.webscience.pizzaorder.repo;

import com.webscience.pizzaorder.model.Order;
import com.webscience.pizzaorder.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * NOTES:
 * This class is a repo wrapping a very simple implementation of an in memory storage.
 * The implementation could be changed to use a better storage (e.g. relational database, ElasticSearch)
 * without changing the "service" logic.
 */
@Component
public class OrderRepo {

    private final List<Order> orders = new ArrayList<>();
    private String orderIdInProgress = null;

    /**
     * NOTES:
     * This implementation of unique id generation is very simple and local,
     * it works only with a single instance of the APIs service.
     */
    public String generate() {
        return UUID.randomUUID().toString();
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public Optional<Order> getOrderById(String orderId) {
        return orders.stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst();
    }

    public List<Order> getNotProcessed() {
        return orders.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.WAITING)
                .collect(Collectors.toList());
    }

    public List<Order> getAll() {
        return orders;
    }

    public String getOrderInProgress() {
        return orderIdInProgress;
    }

    public void setOrderInProgress(Order order) {
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        orderIdInProgress = order.getId();
    }

    public void setOrderCompleted(Order order) {
        order.setOrderStatus(OrderStatus.COMPLETED);
        orderIdInProgress = null;
    }

    //for testing only
    public void clear() {
        orderIdInProgress = null;
        orders.clear();
    }
}
