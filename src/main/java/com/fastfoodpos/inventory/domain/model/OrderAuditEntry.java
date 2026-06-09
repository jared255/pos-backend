package com.fastfoodpos.inventory.domain.model;

import java.time.Instant;

public class OrderAuditEntry {
    private final Integer orderId;
    private final Integer orderNumber;
    private final Integer userId;
    private final String eventType;
    private final String result;
    private final String message;
    private final Instant occurredAt;

    public OrderAuditEntry(Integer orderId, Integer orderNumber, Integer userId, String eventType, String result, String message, Instant occurredAt) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.eventType = eventType;
        this.result = result;
        this.message = message;
        this.occurredAt = occurredAt;
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

    public String getEventType() {
        return eventType;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
