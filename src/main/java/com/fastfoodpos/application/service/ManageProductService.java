package com.fastfoodpos.application.service;

import com.fastfoodpos.domain.model.Product;
import com.fastfoodpos.domain.port.in.ManageProductPort;
import com.fastfoodpos.domain.port.out.ProductRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ManageProductService implements ManageProductPort {
    private static final Logger logger = LoggerFactory.getLogger(ManageProductService.class);
    private final ProductRepositoryPort repository;

    public ManageProductService(ProductRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> findAll(){
        logger.info("Listando todos los productos");
        return repository.findAll();
    }

    @Override
    public Optional<Product> findById(Integer id) {
        logger.info("Producto por id: {}",id);
        return repository.findById(id);
    }

    @Override
    public Integer save(Product product) {
        logger.info("Guardando producto: {}", product.getName());
        return repository.save(product);
    }

    @Override
    public void delete(Integer id) {
        logger.info("Eliminando producto por id: {}", id);
        repository.deleteById(id);
    }
}
