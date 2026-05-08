package com.fastfoodpos.domain.port.in;

import com.fastfoodpos.domain.model.AppUser;

import java.util.List;
import java.util.Optional;

public interface ManageAppUserPort {
    List<AppUser> findAll();

    Optional<AppUser> findById(Integer id);

    Integer save(AppUser appUser);

    void delete(Integer id);
}
