package com.fastfoodpos.application.service;

import com.fastfoodpos.domain.exception.DuplicateMenuItemException;
import com.fastfoodpos.domain.exception.MenuItemNotFoundException;
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
    public List<Product> listMenuItems() {
        logger.info("Listando items del menu");
        return repository.findAll();
    }

    @Override
    public List<Product> listAvailableMenuItems() {
        logger.info("Listando items disponibles para venta");
        return repository.findAvailable();
    }

    @Override
    public List<Product> listMenuItemsByCategory(Integer categoryId) {
        logger.info("Listando items por categoria {}", categoryId);
        return repository.findByCategory(categoryId);
    }

    @Override
    public Optional<Product> findMenuItemById(Integer id) {
        logger.info("Item de menu por id: {}", id);
        return repository.findById(id);
    }

    @Override
    public Integer registerMenuItem(Product product) {
        logger.info("Registrando item de menu: {}", product.getName());
        normalizeStatusForStock(product);
        if (repository.existsByName(product.getName())) {
            throw new DuplicateMenuItemException(product.getName());
        }
        return repository.insert(product);
    }

    @Override
    public Integer updateMenuItem(Product product) {
        logger.info("Actualizando item de menu con id {}", product.getId());
        ensureMenuItemExists(product.getId());
        normalizeStatusForStock(product);
        return repository.update(product);
    }

    @Override
    public void changeMenuItemStatus(Integer id, Product.ProductStatus newStatus) {
        logger.info("Cambiando estado de item de menu {} a {}", id, newStatus);
        ensureMenuItemExists(id);
        repository.changeStatus(id, newStatus);
    }

    private void normalizeStatusForStock(Product product) {
        if (product.getStock() != null && product.getStock() == 0) {
            product.setStatus(Product.ProductStatus.AGOTADO);
            return;
        }
        if (product.getStatus() == null) {
            product.setStatus(Product.ProductStatus.ACTIVO);
        }
    }

    private void ensureMenuItemExists(Integer id) {
        if (repository.findById(id).isEmpty()) {
            throw new MenuItemNotFoundException(id);
        }
    }
}
