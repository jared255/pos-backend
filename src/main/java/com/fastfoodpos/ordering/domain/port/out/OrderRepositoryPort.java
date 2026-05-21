package com.fastfoodpos.ordering.domain.port.out;

import com.fastfoodpos.ordering.domain.model.Order;
import com.fastfoodpos.ordering.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Integer insert(Order order);

    Optional<Order> findById(Integer id);

    List<Order> findByStatuses(List<OrderStatus> statuses);

    void updateStatus(Integer id, OrderStatus status);

    Integer resolveStatusId(OrderStatus status);
}
