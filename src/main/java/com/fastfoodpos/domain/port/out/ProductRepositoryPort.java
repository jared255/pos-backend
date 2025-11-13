package com.fastfoodpos.domain.port.out;
import com.fastfoodpos.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    List<Product>findAll();
    Optional<Product> findById(Integer id);
    Integer save(Product product); // sirve para insert y update
    void deleteById(Integer id);
}