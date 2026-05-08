package com.fastfoodpos.domain.port.in;

import com.fastfoodpos.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface ManageRolePort {
    List<Role> findAll();

    Optional<Role> findById(Integer id);

    Integer save(Role role);

    void delete(Integer id);
}
