package com.fastfoodpos.inventory.domain.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Integer productId, Integer requested, Integer available) {
        super("Stock insuficiente para producto " + productId + ". Solicitado: " + requested + ", disponible: " + available);
    }
}
