package com.fastfoodpos.ordering.infrastructure.web;

import com.fastfoodpos.ordering.domain.model.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderCreateRequest {
    @NotNull(message = "El usuario es obligatorio")
    private Integer userId;
    @NotEmpty(message = "El pedido debe incluir al menos un item")
    @Valid
    private List<OrderItemRequest> items;

    public Order toDomain() {
        Order order = new Order();
        order.setUserId(userId);
        order.setItems(items.stream().map(OrderItemRequest::toDomain).toList());
        return order;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
