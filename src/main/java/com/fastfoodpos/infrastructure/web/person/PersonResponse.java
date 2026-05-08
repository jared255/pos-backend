package com.fastfoodpos.infrastructure.web.person;

import com.fastfoodpos.domain.model.Person;

public class PersonResponse {
    private Integer id;
    private String firstName;
    private String paternalLastName;
    private String maternalLastName;
    private String phone;

    public static PersonResponse fromDomain(Person person) {
        PersonResponse response = new PersonResponse();
        response.setId(person.getId());
        response.setFirstName(person.getFirstName());
        response.setPaternalLastName(person.getPaternalLastName());
        response.setMaternalLastName(person.getMaternalLastName());
        response.setPhone(person.getPhone());
        return response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPaternalLastName() {
        return paternalLastName;
    }

    public void setPaternalLastName(String paternalLastName) {
        this.paternalLastName = paternalLastName;
    }

    public String getMaternalLastName() {
        return maternalLastName;
    }

    public void setMaternalLastName(String maternalLastName) {
        this.maternalLastName = maternalLastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
