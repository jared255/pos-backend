package com.fastfoodpos.application.service;

import com.fastfoodpos.domain.model.AppUser;
import com.fastfoodpos.domain.port.in.ManageAppUserPort;
import com.fastfoodpos.domain.port.out.AppUserRepositoryPort;
import com.fastfoodpos.domain.port.out.PasswordHasherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ManageAppUserService implements ManageAppUserPort {
    private static final Logger logger = LoggerFactory.getLogger(ManageAppUserService.class);
    private final AppUserRepositoryPort repository;
    private final PasswordHasherPort passwordHasher;

    public ManageAppUserService(AppUserRepositoryPort repository, PasswordHasherPort passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public List<AppUser> findAll() {
        logger.info("Listando usuarios");
        return repository.findAll();
    }

    @Override
    public Optional<AppUser> findById(Integer id) {
        logger.info("Buscando usuario por id: {}", id);
        return repository.findById(id);
    }

    @Override
    public Integer save(AppUser appUser) {
        logger.info("Guardando usuario: {}", appUser.getUsername());
        if (appUser.getPassword() != null && !appUser.getPassword().isBlank()) {
            appUser.setPassword(passwordHasher.hash(appUser.getPassword()));
        }
        return repository.save(appUser);
    }

    @Override
    public void delete(Integer id) {
        logger.info("Eliminando usuario por id: {}", id);
        repository.deleteById(id);
    }
}
