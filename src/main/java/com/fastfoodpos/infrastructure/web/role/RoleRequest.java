package com.fastfoodpos.infrastructure.web.role;

import com.fastfoodpos.domain.model.Role;
import jakarta.validation.constraints.NotBlank;

public class RoleRequest {
    @NotBlank(message = "El nombre del rol es obligatorio")
    private String name;

    public Role toDomain() {
        Role role = new Role();
        role.setName(normalizeName(name));
        return role;
    }

    public Role toDomain(Integer id) {
        Role role = toDomain();
        role.setId(id);
        return role;
    }

    private String normalizeName(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
