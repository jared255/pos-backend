package com.fastfoodpos.domain.port.out;

import com.fastfoodpos.domain.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepositoryPort {
    List<Person> findAll();

    Optional<Person> findById(Integer id);

    Integer save(Person person);

    void deleteById(Integer id);
}
