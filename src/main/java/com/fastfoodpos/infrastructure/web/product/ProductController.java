package com.fastfoodpos.infrastructure.web.product;

import com.fastfoodpos.domain.port.in.ManageProductPort;
import org.springframework.http.HttpStatus;
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
        return manageProductPort.findAll()
                .stream()
                .map(ProductResponse::fromDomain)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Integer id) {
        return manageProductPort.findById(id)
                .map(ProductResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ProductRequest request) {
        Integer id = manageProductPort.save(request.toDomain());
        return ResponseEntity.created(URI.create("/api/products/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody ProductRequest request) {
        if (manageProductPort.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        manageProductPort.save(request.toDomain(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (manageProductPort.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        manageProductPort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
