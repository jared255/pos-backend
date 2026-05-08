package com.fastfoodpos.application.service;

import com.fastfoodpos.domain.model.Role;
import com.fastfoodpos.domain.port.in.ManageRolePort;
import com.fastfoodpos.domain.port.out.RoleRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ManageRoleService implements ManageRolePort {
    private static final Logger logger = LoggerFactory.getLogger(ManageRoleService.class);
    private final RoleRepositoryPort repository;

    public ManageRoleService(RoleRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Role> findAll() {
        logger.info("Listando roles");
        return repository.findAll();
    }

    @Override
    public Optional<Role> findById(Integer id) {
        logger.info("Buscando rol por id: {}", id);
        return repository.findById(id);
    }

    @Override
    public Integer save(Role role) {
        logger.info("Guardando rol: {}", role.getName());
        return repository.save(role);
    }

    @Override
    public void delete(Integer id) {
        logger.info("Eliminando rol por id: {}", id);
        repository.deleteById(id);
    }
}
