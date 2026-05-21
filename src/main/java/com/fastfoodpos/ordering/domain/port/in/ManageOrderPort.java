package com.fastfoodpos.ordering.domain.port.in;

import com.fastfoodpos.ordering.domain.model.Order;
import com.fastfoodpos.ordering.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface ManageOrderPort {
    Integer createOrder(Order order);

    Optional<Order> findById(Integer id);

    List<Order> findByStatuses(List<OrderStatus> statuses);

    void changeStatus(Integer id, OrderStatus newStatus);
}
