package com.fastfoodpos.application.service;

import com.fastfoodpos.domain.model.Person;
import com.fastfoodpos.domain.port.in.ManagePersonPort;
import com.fastfoodpos.domain.port.out.PersonRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ManagePersonService implements ManagePersonPort {
    private static final Logger logger = LoggerFactory.getLogger(ManagePersonService.class);
    private final PersonRepositoryPort repository;

    public ManagePersonService(PersonRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Person> findAll() {
        logger.info("Listando personas");
        return repository.findAll();
    }

    @Override
    public Optional<Person> findById(Integer id) {
        logger.info("Buscando persona por id: {}", id);
        return repository.findById(id);
    }

    @Override
    public Integer save(Person person) {
        logger.info("Guardando persona: {}", person.getFirstName());
        return repository.save(person);
    }

    @Override
    public void delete(Integer id) {
        logger.info("Eliminando persona por id: {}", id);
        repository.deleteById(id);
    }
}
