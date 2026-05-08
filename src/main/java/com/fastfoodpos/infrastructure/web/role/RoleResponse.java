package com.fastfoodpos.infrastructure.web.role;

import com.fastfoodpos.domain.model.Role;

public class RoleResponse {
    private Integer id;
    private String name;

    public static RoleResponse fromDomain(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        return response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
