package com.fastfoodpos.infrastructure.web.role;

import com.fastfoodpos.domain.model.Role;
import com.fastfoodpos.domain.port.in.ManageRolePort;
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

class RoleControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ManageRolePort manageRolePort;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        manageRolePort = mock(ManageRolePort.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new RoleController(manageRolePort))
                .setControllerAdvice(new RestExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void findAllReturnsRolesFromPort() throws Exception {
        when(manageRolePort.findAll()).thenReturn(List.of(new Role(1, "ADMIN"), new Role(2, "USER")));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[1].name").value("USER"));
    }

    @Test
    void createNormalizesRoleNameAndReturnsLocation() throws Exception {
        when(manageRolePort.save(any(Role.class))).thenReturn(3);

        RoleRequest request = new RoleRequest();
        request.setName("cashier");

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/roles/3"));

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(manageRolePort).save(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals("CASHIER", captor.getValue().getName());
    }

    @Test
    void createRejectsInvalidRequest() throws Exception {
        RoleRequest request = new RoleRequest();
        request.setName("");

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasItem("name: El nombre del rol es obligatorio")));

        verify(manageRolePort, never()).save(any(Role.class));
    }

    @Test
    void updateReturnsNoContentWhenRoleExists() throws Exception {
        when(manageRolePort.findById(1)).thenReturn(Optional.of(new Role(1, "ADMIN")));

        RoleRequest request = new RoleRequest();
        request.setName("admin");

        mockMvc.perform(put("/api/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(manageRolePort).save(any(Role.class));
    }

    @Test
    void deleteReturnsNoContentWhenRoleExists() throws Exception {
        when(manageRolePort.findById(2)).thenReturn(Optional.of(new Role(2, "USER")));

        mockMvc.perform(delete("/api/roles/2"))
                .andExpect(status().isNoContent());

        verify(manageRolePort).delete(2);
    }
}
