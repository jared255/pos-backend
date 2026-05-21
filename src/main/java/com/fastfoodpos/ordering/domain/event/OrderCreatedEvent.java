package com.fastfoodpos.ordering.domain.event;

import java.time.Instant;

public class OrderCreatedEvent {
    private final Integer orderId;
    private final Integer orderNumber;
    private final Integer userId;
    private final Instant occurredAt;

    public OrderCreatedEvent(Integer orderId, Integer orderNumber, Integer userId) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.occurredAt = Instant.now();
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public Integer getUserId() {
        return userId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
