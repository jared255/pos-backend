package com.fastfoodpos.ordering.domain.event;

import java.time.Instant;

public class OrderDeliveredEvent {
    private final Integer orderId;
    private final Integer orderNumber;
    private final Instant occurredAt;

    public OrderDeliveredEvent(Integer orderId, Integer orderNumber) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.occurredAt = Instant.now();
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
