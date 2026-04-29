package com.fastfoodpos.domain.model;
import java.math.BigDecimal;

public class Product{
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private ProductStatus status;
    private Integer categoryId;

    public Product() {

    }

    public enum ProductStatus{
        ACTIVO,
        INACTIVO,
        AGOTADO
    }

public Product(String name, String description,BigDecimal price,Integer stock,ProductStatus status){
        this.name=name;
        this.description=description;
        this.price=price;
        this.stock=stock;
        this.status=status;
}

    public Product(Integer id, String name, String description, BigDecimal price, Integer stock, ProductStatus status, Integer categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.categoryId = categoryId;
    }
// Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

}