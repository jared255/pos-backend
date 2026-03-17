package com.fastfoodpos.infrastructure.api;

import com.fastfoodpos.domain.model.Product;
import com.fastfoodpos.domain.port.in.ManageProductPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ManageProductPort productPort;

    public ProductController(ManageProductPort productPort) {
        this.productPort = productPort;
    }

    @GetMapping
    public List<Product> findAll() {
        return productPort.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Integer id) {
        return productPort.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody Product product){
        Integer id = productPort.save(product);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody Product product){
        product.setId(id);
        productPort.save(product);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Integer id){
        productPort.delete(id);
        return ResponseEntity.ok().build();
    }

}
