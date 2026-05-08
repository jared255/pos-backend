package com.fastfoodpos.domain.model;

import java.util.ArrayList;
import java.util.List;

public class AppUser {
    private Integer id;
    private String username;
    private String password;
    private Integer personId;
    private Person person;
    private List<Role> roles = new ArrayList<>();

    public AppUser() {
    }

    public AppUser(Integer id, String username, String password, Integer personId, Person person, List<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.personId = personId;
        this.person = person;
        this.roles = roles == null ? new ArrayList<>() : roles;
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles == null ? new ArrayList<>() : roles;
    }
}
