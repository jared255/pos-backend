package com.fastfoodpos.infrastructure.web.user;

import com.fastfoodpos.domain.port.in.ManageAppUserPort;
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
@RequestMapping("/api/users")
public class AppUserController {
    private final ManageAppUserPort manageAppUserPort;

    public AppUserController(ManageAppUserPort manageAppUserPort) {
        this.manageAppUserPort = manageAppUserPort;
    }

    @GetMapping
    public List<AppUserResponse> findAll() {
        return manageAppUserPort.findAll()
                .stream()
                .map(AppUserResponse::fromDomain)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserResponse> findById(@PathVariable Integer id) {
        return manageAppUserPort.findById(id)
                .map(AppUserResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody AppUserCreateRequest request) {
        Integer id = manageAppUserPort.save(request.toDomain());
        return ResponseEntity.created(URI.create("/api/users/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @Valid @RequestBody AppUserUpdateRequest request) {
        if (manageAppUserPort.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        manageAppUserPort.save(request.toDomain(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (manageAppUserPort.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        manageAppUserPort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
