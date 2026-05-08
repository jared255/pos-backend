package com.fastfoodpos.domain.model;

public class Person {
    private Integer id;
    private String firstName;
    private String paternalLastName;
    private String maternalLastName;
    private String phone;

    public Person() {
    }

    public Person(Integer id, String firstName, String paternalLastName, String maternalLastName, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.paternalLastName = paternalLastName;
        this.maternalLastName = maternalLastName;
        this.phone = phone;
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
