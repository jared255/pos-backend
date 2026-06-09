package com.fastfoodpos.inventory.domain.model;

import java.time.Instant;
import java.util.List;

public class OrderCreatedStockAdjustment {
    private final Integer orderId;
    private final Integer orderNumber;
    private final Integer userId;
    private final List<OrderCreatedStockItem> items;
    private final Instant occurredAt;

    public OrderCreatedStockAdjustment(Integer orderId, Integer orderNumber, Integer userId, List<OrderCreatedStockItem> items, Instant occurredAt) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.items = items == null ? List.of() : List.copyOf(items);
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

    public List<OrderCreatedStockItem> getItems() {
        return items;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
