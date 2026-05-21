package com.fastfoodpos.ordering.domain.exception;

import com.fastfoodpos.ordering.domain.model.OrderStatus;

public class InvalidOrderStatusTransitionException extends RuntimeException {
    public InvalidOrderStatusTransitionException(OrderStatus currentStatus, OrderStatus targetStatus) {
        super("Transicion de estado no permitida: " + currentStatus + " -> " + targetStatus);
    }
}
