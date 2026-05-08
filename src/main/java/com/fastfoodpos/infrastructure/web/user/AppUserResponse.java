package com.fastfoodpos.infrastructure.web.user;

import com.fastfoodpos.domain.model.AppUser;
import com.fastfoodpos.infrastructure.web.person.PersonResponse;
import com.fastfoodpos.infrastructure.web.role.RoleResponse;

import java.util.List;

public class AppUserResponse {
    private Integer id;
    private String username;
    private Integer personId;
    private PersonResponse person;
    private List<RoleResponse> roles;

    public static AppUserResponse fromDomain(AppUser appUser) {
        AppUserResponse response = new AppUserResponse();
        response.setId(appUser.getId());
        response.setUsername(appUser.getUsername());
        response.setPersonId(appUser.getPersonId());
        if (appUser.getPerson() != null) {
            response.setPerson(PersonResponse.fromDomain(appUser.getPerson()));
        }
        response.setRoles(appUser.getRoles()
                .stream()
                .map(RoleResponse::fromDomain)
                .toList());
        return response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public PersonResponse getPerson() {
        return person;
    }

    public void setPerson(PersonResponse person) {
        this.person = person;
    }

    public List<RoleResponse> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleResponse> roles) {
        this.roles = roles;
    }
}
