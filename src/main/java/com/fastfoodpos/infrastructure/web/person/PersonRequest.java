package com.fastfoodpos.infrastructure.web.person;

import com.fastfoodpos.domain.model.Person;
import jakarta.validation.constraints.NotBlank;

public class PersonRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido paterno es obligatorio")
    private String paternalLastName;

    private String maternalLastName;
    private String phone;

    public Person toDomain() {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setPaternalLastName(paternalLastName);
        person.setMaternalLastName(maternalLastName);
        person.setPhone(phone);
        return person;
    }

    public Person toDomain(Integer id) {
        Person person = toDomain();
        person.setId(id);
        return person;
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
