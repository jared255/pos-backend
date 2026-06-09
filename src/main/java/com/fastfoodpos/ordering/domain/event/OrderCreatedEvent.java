package com.fastfoodpos.ordering.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class OrderCreatedEvent {
    private final Integer orderId;
    private final Integer orderNumber;
    private final Integer userId;
    private final List<Item> items;
    private final Instant occurredAt;

    public OrderCreatedEvent(Integer orderId, Integer orderNumber, Integer userId) {
        this(orderId, orderNumber, userId, List.of());
    }

    public OrderCreatedEvent(Integer orderId, Integer orderNumber, Integer userId, List<Item> items) {
        this(orderId, orderNumber, userId, items, Instant.now());
    }

    public OrderCreatedEvent(Integer orderId, Integer orderNumber, Integer userId, List<Item> items, Instant occurredAt) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.items = items == null ? List.of() : List.copyOf(items);
        this.occurredAt = Objects.requireNonNullElseGet(occurredAt, Instant::now);
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

    public List<Item> getItems() {
        return items;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public static class Item {
        private final Integer productId;
        private final Integer quantity;

        public Item(Integer productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Integer getProductId() {
            return productId;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }
}
