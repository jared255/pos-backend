package com.fastfoodpos.domain.port.in;

import com.fastfoodpos.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ManageProductPort {
    List<Product> listMenuItems();

    List<Product> listAvailableMenuItems();

    List<Product> listMenuItemsByCategory(Integer categoryId);

    Optional<Product> findMenuItemById(Integer id);

    Integer registerMenuItem(Product product);

    Integer updateMenuItem(Product product);

    void changeMenuItemStatus(Integer id, Product.ProductStatus newStatus);
}
