package com.fastfoodpos.domain.port.out;

import com.fastfoodpos.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {
    List<Role> findAll();

    Optional<Role> findById(Integer id);

    Integer save(Role role);

    void deleteById(Integer id);
}
