package com.fastfoodpos.infrastructure.web.product;

import com.fastfoodpos.domain.model.Product;
import com.fastfoodpos.domain.port.in.ManageProductPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ManageProductPort manageProductPort;

    public ProductController(ManageProductPort manageProductPort) {
        this.manageProductPort = manageProductPort;
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return manageProductPort.listMenuItems()
                .stream()
                .map(ProductResponse::fromDomain)
                .toList();
    }

    @GetMapping("/available")
    public List<ProductResponse> findAvailable() {
        return manageProductPort.listAvailableMenuItems()
                .stream()
                .map(ProductResponse::fromDomain)
                .toList();
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponse> findByCategory(@PathVariable Integer categoryId) {
        return manageProductPort.listMenuItemsByCategory(categoryId)
                .stream()
                .map(ProductResponse::fromDomain)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Integer id) {
        return manageProductPort.findMenuItemById(id)
                .map(ProductResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ProductRequest request) {
        Integer id = manageProductPort.registerMenuItem(request.toDomain());
        return ResponseEntity.created(URI.create("/api/products/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @Valid @RequestBody ProductRequest request) {
        manageProductPort.updateMenuItem(request.toDomain(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Integer id, @Valid @RequestBody ProductStatusRequest request) {
        manageProductPort.changeMenuItemStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        manageProductPort.changeMenuItemStatus(id, Product.ProductStatus.INACTIVO);
        return ResponseEntity.noContent().build();
    }
}
