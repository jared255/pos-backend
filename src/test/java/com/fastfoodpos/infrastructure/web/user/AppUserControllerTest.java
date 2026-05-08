package com.fastfoodpos.infrastructure.web.user;

import com.fastfoodpos.domain.model.AppUser;
import com.fastfoodpos.domain.model.Person;
import com.fastfoodpos.domain.model.Role;
import com.fastfoodpos.domain.port.in.ManageAppUserPort;
import com.fastfoodpos.infrastructure.web.error.RestExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppUserControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ManageAppUserPort manageAppUserPort;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        manageAppUserPort = mock(ManageAppUserPort.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AppUserController(manageAppUserPort))
                .setControllerAdvice(new RestExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void findAllReturnsUsersWithoutPassword() throws Exception {
        when(manageAppUserPort.findAll()).thenReturn(List.of(appUser()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].person.firstName").value("Ana"))
                .andExpect(jsonPath("$[0].roles[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void createSavesUserAndReturnsLocation() throws Exception {
        when(manageAppUserPort.save(any(AppUser.class))).thenReturn(4);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/4"));

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(manageAppUserPort).save(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals("admin", captor.getValue().getUsername());
        org.junit.jupiter.api.Assertions.assertEquals(1, captor.getValue().getRoles().get(0).getId());
    }

    @Test
    void createRejectsUserWithoutRole() throws Exception {
        AppUserCreateRequest request = validCreateRequest();
        request.setRoleIds(List.of());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasItem("roleIds: El usuario debe tener al menos un rol")));

        verify(manageAppUserPort, never()).save(any(AppUser.class));
    }

    @Test
    void updateReturnsNoContentWhenUserExists() throws Exception {
        when(manageAppUserPort.findById(3)).thenReturn(Optional.of(appUser()));

        mockMvc.perform(put("/api/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateRequest())))
                .andExpect(status().isNoContent());

        verify(manageAppUserPort).save(any(AppUser.class));
    }

    @Test
    void deleteReturnsNoContentWhenUserExists() throws Exception {
        when(manageAppUserPort.findById(3)).thenReturn(Optional.of(appUser()));

        mockMvc.perform(delete("/api/users/3"))
                .andExpect(status().isNoContent());

        verify(manageAppUserPort).delete(3);
    }

    @Test
    void findByIdDoesNotExposePassword() throws Exception {
        when(manageAppUserPort.findById(3)).thenReturn(Optional.of(appUser()));

        mockMvc.perform(get("/api/users/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", not(hasItem("secret"))))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    private AppUser appUser() {
        Person person = new Person(1, "Ana", "Perez", "Rojas", "70000001");
        return new AppUser(
                3,
                "admin",
                "secret",
                1,
                person,
                List.of(new Role(1, "ADMIN"))
        );
    }

    private AppUserCreateRequest validCreateRequest() {
        AppUserCreateRequest request = new AppUserCreateRequest();
        request.setUsername("admin");
        request.setPassword("secret");
        request.setPersonId(1);
        request.setRoleIds(List.of(1));
        return request;
    }

    private AppUserUpdateRequest validUpdateRequest() {
        AppUserUpdateRequest request = new AppUserUpdateRequest();
        request.setUsername("admin");
        request.setPassword("");
        request.setPersonId(1);
        request.setRoleIds(List.of(1));
        return request;
    }
}
