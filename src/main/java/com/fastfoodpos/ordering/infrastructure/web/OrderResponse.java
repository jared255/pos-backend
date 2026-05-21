package com.fastfoodpos.ordering.infrastructure.web;

import com.fastfoodpos.ordering.domain.model.Order;
import com.fastfoodpos.ordering.domain.model.OrderItem;
import com.fastfoodpos.ordering.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Integer id;
    private Integer orderNumber;
    private Integer userId;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;

    public static OrderResponse fromDomain(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUserId());
        response.setTotal(order.getTotal());
        response.setStatus(order.getStatus());
        response.setOrderDate(order.getOrderDate());
        response.setItems(order.getItems().stream().map(OrderItemResponse::fromDomain).toList());
        return response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}
