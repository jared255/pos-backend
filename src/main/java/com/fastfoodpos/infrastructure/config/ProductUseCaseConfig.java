package com.fastfoodpos.infrastructure.config;

import com.fastfoodpos.application.service.ManageProductService;
import com.fastfoodpos.application.service.ManageAppUserService;
import com.fastfoodpos.application.service.ManagePersonService;
import com.fastfoodpos.application.service.ManageRoleService;
import com.fastfoodpos.domain.port.in.ManageAppUserPort;
import com.fastfoodpos.domain.port.in.ManagePersonPort;
import com.fastfoodpos.domain.port.in.ManageProductPort;
import com.fastfoodpos.domain.port.in.ManageRolePort;
import com.fastfoodpos.domain.port.out.AppUserRepositoryPort;
import com.fastfoodpos.domain.port.out.PasswordHasherPort;
import com.fastfoodpos.domain.port.out.PersonRepositoryPort;
import com.fastfoodpos.domain.port.out.ProductRepositoryPort;
import com.fastfoodpos.domain.port.out.RoleRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductUseCaseConfig {
    @Bean
    public ManageProductPort manageProductPort(ProductRepositoryPort productRepositoryPort) {
        return new ManageProductService(productRepositoryPort);
    }

    @Bean
    public ManagePersonPort managePersonPort(PersonRepositoryPort personRepositoryPort) {
        return new ManagePersonService(personRepositoryPort);
    }

    @Bean
    public ManageRolePort manageRolePort(RoleRepositoryPort roleRepositoryPort) {
        return new ManageRoleService(roleRepositoryPort);
    }

    @Bean
    public ManageAppUserPort manageAppUserPort(AppUserRepositoryPort appUserRepositoryPort, PasswordHasherPort passwordHasherPort) {
        return new ManageAppUserService(appUserRepositoryPort, passwordHasherPort);
    }
}
