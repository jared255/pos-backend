package com.fastfoodpos.infrastructure.web;

import com.fastfoodpos.domain.model.Product;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ManageProductPort manageProductPort;

    public ProductController(ManageProductPort manageProductPort) {
        this.manageProductPort = manageProductPort;
    }

    @GetMapping
    public List<Product> findAll() {
        return manageProductPort.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Integer id) {
        Optional<Product> product = manageProductPort.findById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        product.setId(null);
        Integer id = manageProductPort.save(product);

        return manageProductPort.findById(id)
                .map(savedProduct -> ResponseEntity
                        .created(URI.create("/products/" + savedProduct.getId()))
                        .body(savedProduct))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Integer id, @RequestBody Product product) {
        if (manageProductPort.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        product.setId(id);
        manageProductPort.save(product);

        return manageProductPort.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Integer id) {
        if (manageProductPort.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        manageProductPort.delete(id);
        return ResponseEntity.ok(Map.of("message", "Producto eliminado correctamente"));
    }
}
