package com.fastfoodpos.inventory.domain.model;

public class OrderCreatedStockItem {
    private final Integer productId;
    private final Integer quantity;

    public OrderCreatedStockItem(Integer productId, Integer quantity) {
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
