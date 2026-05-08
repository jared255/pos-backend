package com.fastfoodpos.infrastructure.web.role;

import com.fastfoodpos.domain.port.in.ManageRolePort;
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
@RequestMapping("/api/roles")
public class RoleController {
    private final ManageRolePort manageRolePort;

    public RoleController(ManageRolePort manageRolePort) {
        this.manageRolePort = manageRolePort;
    }

    @GetMapping
    public List<RoleResponse> findAll() {
        return manageRolePort.findAll()
                .stream()
                .map(RoleResponse::fromDomain)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> findById(@PathVariable Integer id) {
        return manageRolePort.findById(id)
                .map(RoleResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody RoleRequest request) {
        Integer id = manageRolePort.save(request.toDomain());
        return ResponseEntity.created(URI.create("/api/roles/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @Valid @RequestBody RoleRequest request) {
        if (manageRolePort.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        manageRolePort.save(request.toDomain(id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (manageRolePort.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        manageRolePort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
