package com.webscience.pizzaorder.model;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class Order {
    private final String id;
    private final String userName;
    private final List<OrderEntry> orderEntries;
    private final OffsetDateTime insertTs;

    private OrderStatus orderStatus;

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
