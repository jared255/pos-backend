package com.fastfoodpos.infrastructure.web.person;

import com.fastfoodpos.domain.port.in.ManagePersonPort;
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
@RequestMapping("/api/persons")
public class PersonController {
    private final ManagePersonPort managePersonPort;

    public PersonController(ManagePersonPort managePersonPort) {
        this.managePersonPort = managePersonPort;
    }

    @GetMapping
    public List<PersonResponse> findAll() {
        return managePersonPort.findAll()
                .stream()
                .map(PersonResponse::fromDomain)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> findById(@PathVariable Integer id) {
        return managePersonPort.findById(id)
                .map(PersonResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody PersonRequest request) {
        Integer id = managePersonPort.save(request.toDomain());
        return ResponseEntity.created(URI.create("/api/persons/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @Valid @RequestBody PersonRequest request) {
        if (managePersonPort.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        managePersonPort.save(request.toDomain(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (managePersonPort.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        managePersonPort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
