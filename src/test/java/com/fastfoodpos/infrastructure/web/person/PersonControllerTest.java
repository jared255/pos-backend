package com.fastfoodpos.infrastructure.web.person;

import com.fastfoodpos.domain.model.Person;
import com.fastfoodpos.domain.port.in.ManagePersonPort;
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

class PersonControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ManagePersonPort managePersonPort;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        managePersonPort = mock(ManagePersonPort.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new PersonController(managePersonPort))
                .setControllerAdvice(new RestExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void findAllReturnsPersonsFromPort() throws Exception {
        when(managePersonPort.findAll()).thenReturn(List.of(person()));

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Ana"));
    }

    @Test
    void createSavesPersonAndReturnsLocation() throws Exception {
        when(managePersonPort.save(any(Person.class))).thenReturn(7);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/persons/7"));

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(managePersonPort).save(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals("Ana", captor.getValue().getFirstName());
    }

    @Test
    void createRejectsInvalidRequest() throws Exception {
        PersonRequest request = validRequest();
        request.setFirstName("");

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasItem("firstName: El nombre es obligatorio")));

        verify(managePersonPort, never()).save(any(Person.class));
    }

    @Test
    void updateReturnsNoContentWhenPersonExists() throws Exception {
        when(managePersonPort.findById(1)).thenReturn(Optional.of(person()));

        mockMvc.perform(put("/api/persons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNoContent());

        verify(managePersonPort).save(any(Person.class));
    }

    @Test
    void deleteReturnsNoContentWhenPersonExists() throws Exception {
        when(managePersonPort.findById(1)).thenReturn(Optional.of(person()));

        mockMvc.perform(delete("/api/persons/1"))
                .andExpect(status().isNoContent());

        verify(managePersonPort).delete(1);
    }

    private Person person() {
        return new Person(1, "Ana", "Perez", "Rojas", "70000001");
    }

    private PersonRequest validRequest() {
        PersonRequest request = new PersonRequest();
        request.setFirstName("Ana");
        request.setPaternalLastName("Perez");
        request.setMaternalLastName("Rojas");
        request.setPhone("70000001");
        return request;
    }
}
