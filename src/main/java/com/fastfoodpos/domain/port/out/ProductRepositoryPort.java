package com.fastfoodpos.domain.port.out;
import com.fastfoodpos.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    List<Product> findAll();

    List<Product> findAvailable();

    List<Product> findByCategory(Integer categoryId);

    Optional<Product> findById(Integer id);

    Integer insert(Product product);

    Integer update(Product product);

    boolean existsByName(String name);

    Integer changeStatus(Integer id, Product.ProductStatus status);

    void deleteById(Integer id);
}
