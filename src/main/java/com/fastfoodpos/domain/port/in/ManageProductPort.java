package com.fastfoodpos.domain.port.in;

import com.fastfoodpos.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ManageProductPort {
    List<Product> findAll();

    Optional<Product> findById(Integer id);

    Integer save(Product product); // insert/update

    void delete(Integer id);
}