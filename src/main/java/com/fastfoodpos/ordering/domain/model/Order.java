package com.fastfoodpos.ordering.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer id;
    private Integer orderNumber;
    private Integer userId;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    public Order(Integer id, Integer orderNumber, Integer userId, BigDecimal total, OrderStatus status, LocalDateTime orderDate, List<OrderItem> items) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.total = total;
        this.status = status;
        this.orderDate = orderDate;
        this.items = items == null ? new ArrayList<>() : items;
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items == null ? new ArrayList<>() : items;
    }
}
