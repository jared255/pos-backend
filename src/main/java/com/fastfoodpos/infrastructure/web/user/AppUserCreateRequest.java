package com.fastfoodpos.infrastructure.web.user;

import com.fastfoodpos.domain.model.AppUser;
import com.fastfoodpos.domain.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AppUserCreateRequest {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "La contrasena es obligatoria")
    private String password;

    @NotNull(message = "La persona es obligatoria")
    private Integer personId;

    @NotEmpty(message = "El usuario debe tener al menos un rol")
    private List<Integer> roleIds;

    public AppUser toDomain() {
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setPersonId(personId);
        appUser.setRoles(mapRoles(roleIds));
        return appUser;
    }

    private List<Role> mapRoles(List<Integer> ids) {
        return ids.stream()
                .map(id -> new Role(id, null))
                .toList();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
