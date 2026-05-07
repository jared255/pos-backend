package com.fastfoodpos.infrastructure.web.product;

import com.fastfoodpos.domain.model.Product;
import com.fastfoodpos.domain.port.in.ManageProductPort;
import com.fastfoodpos.infrastructure.web.error.RestExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
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

class ProductControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ManageProductPort manageProductPort;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        manageProductPort = mock(ManageProductPort.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductController(manageProductPort))
                .setControllerAdvice(new RestExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void findAllReturnsProductsFromPort() throws Exception {
        when(manageProductPort.findAll()).thenReturn(List.of(product()));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].name").value("Coca-Cola"))
                .andExpect(jsonPath("$[0].status").value("ACTIVO"));
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() throws Exception {
        when(manageProductPort.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSavesProductAndReturnsCreatedLocation() throws Exception {
        when(manageProductPort.save(any(Product.class))).thenReturn(12);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/12"));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(manageProductPort).save(captor.capture());
        Product saved = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("Coca-Cola", saved.getName());
        org.junit.jupiter.api.Assertions.assertEquals(Product.ProductStatus.ACTIVO, saved.getStatus());
    }

    @Test
    void createRejectsInvalidRequest() throws Exception {
        ProductRequest request = validRequest();
        request.setName("");
        request.setPrice(BigDecimal.valueOf(-1));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Solicitud invalida"))
                .andExpect(jsonPath("$.details", hasItem("name: El nombre del producto es obligatorio")))
                .andExpect(jsonPath("$.details", hasItem("price: El precio no puede ser negativo")));

        verify(manageProductPort, never()).save(any(Product.class));
    }

    @Test
    void updateReturnsNoContentWhenProductExists() throws Exception {
        when(manageProductPort.findById(5)).thenReturn(Optional.of(product()));
        when(manageProductPort.save(any(Product.class))).thenReturn(5);

        mockMvc.perform(put("/api/products/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNoContent());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(manageProductPort).save(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(5, captor.getValue().getId());
    }

    @Test
    void deleteReturnsNoContentWhenProductExists() throws Exception {
        when(manageProductPort.findById(5)).thenReturn(Optional.of(product()));

        mockMvc.perform(delete("/api/products/5"))
                .andExpect(status().isNoContent());

        verify(manageProductPort).delete(5);
    }

    @Test
    void deleteReturnsNotFoundWhenProductDoesNotExist() throws Exception {
        when(manageProductPort.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound());

        verify(manageProductPort, never()).delete(99);
    }

    private Product product() {
        return new Product(
                5,
                "Coca-Cola",
                "Refresco de cola de 355ml.",
                BigDecimal.valueOf(18.50),
                100,
                Product.ProductStatus.ACTIVO,
                1
        );
    }

    private ProductRequest validRequest() {
        ProductRequest request = new ProductRequest();
        request.setName("Coca-Cola");
        request.setDescription("Refresco de cola de 355ml.");
        request.setPrice(BigDecimal.valueOf(18.50));
        request.setStock(100);
        request.setStatus(Product.ProductStatus.ACTIVO);
        request.setCategoryId(1);
        return request;
    }
}
