package com.fastfoodpos.inventory.domain.exception;

public class InventoryProductNotFoundException extends RuntimeException {
    public InventoryProductNotFoundException(Integer productId) {
        super("Producto no encontrado en inventario: " + productId);
    }
}
