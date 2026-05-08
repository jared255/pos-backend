package com.fastfoodpos.domain.port.out;

import com.fastfoodpos.domain.model.AppUser;

import java.util.List;
import java.util.Optional;

public interface AppUserRepositoryPort {
    List<AppUser> findAll();

    Optional<AppUser> findById(Integer id);

    Integer save(AppUser appUser);

    void deleteById(Integer id);
}
