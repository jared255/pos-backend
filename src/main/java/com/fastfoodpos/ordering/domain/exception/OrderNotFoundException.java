package com.fastfoodpos.ordering.domain.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Integer id) {
        super("No existe pedido con id " + id);
    }
}
