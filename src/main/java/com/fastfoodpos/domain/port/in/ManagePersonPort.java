package com.fastfoodpos.domain.port.in;

import com.fastfoodpos.domain.model.Person;

import java.util.List;
import java.util.Optional;

public interface ManagePersonPort {
    List<Person> findAll();

    Optional<Person> findById(Integer id);

    Integer save(Person person);

    void delete(Integer id);
}
