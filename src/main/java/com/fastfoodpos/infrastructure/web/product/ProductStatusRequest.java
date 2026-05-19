package com.fastfoodpos.infrastructure.web.product;

import com.fastfoodpos.domain.model.Product;
import jakarta.validation.constraints.NotNull;

public class ProductStatusRequest {
    @NotNull(message = "El estado es obligatorio")
    private Product.ProductStatus status;

    public Product.ProductStatus getStatus() {
        return status;
    }

    public void setStatus(Product.ProductStatus status) {
        this.status = status;
    }
}
