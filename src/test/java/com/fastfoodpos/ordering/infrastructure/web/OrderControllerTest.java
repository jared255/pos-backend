package com.fastfoodpos.ordering.infrastructure.web;

import com.fastfoodpos.infrastructure.web.error.RestExceptionHandler;
import com.fastfoodpos.ordering.domain.exception.InvalidOrderStatusTransitionException;
import com.fastfoodpos.ordering.domain.model.Order;
import com.fastfoodpos.ordering.domain.model.OrderItem;
import com.fastfoodpos.ordering.domain.model.OrderStatus;
import com.fastfoodpos.ordering.domain.port.in.ManageOrderPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ManageOrderPort manageOrderPort;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        manageOrderPort = mock(ManageOrderPort.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new OrderController(manageOrderPort))
                .setControllerAdvice(new RestExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createReturnsCreatedLocation() throws Exception {
        when(manageOrderPort.createOrder(any(Order.class))).thenReturn(15);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/orders/15"));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(manageOrderPort).createOrder(captor.capture());
        Order saved = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(1, saved.getUserId());
        org.junit.jupiter.api.Assertions.assertEquals(2, saved.getItems().get(0).getProductId());
    }

    @Test
    void createRejectsEmptyItems() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(1);
        request.setItems(List.of());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasItem("items: El pedido debe incluir al menos un item")));

        verify(manageOrderPort, never()).createOrder(any(Order.class));
    }

    @Test
    void findByIdReturnsOrder() throws Exception {
        when(manageOrderPort.findById(9)).thenReturn(Optional.of(order()));

        mockMvc.perform(get("/api/orders/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.orderNumber").value(1001))
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }

    @Test
    void findByStatusReturnsOrders() throws Exception {
        when(manageOrderPort.findByStatuses(List.of(OrderStatus.PREPARING, OrderStatus.READY)))
                .thenReturn(List.of(order()));

        mockMvc.perform(get("/api/orders")
                        .param("status", "PREPARING", "READY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(9));
    }

    @Test
    void changeStatusReturnsConflictWhenTransitionIsInvalid() throws Exception {
        when(manageOrderPort.findById(9)).thenReturn(Optional.of(order()));
        when(manageOrderPort.findByStatuses(List.of(OrderStatus.PREPARING))).thenReturn(List.of(order()));

        whenInvalidTransition();

        mockMvc.perform(put("/api/orders/9/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DELIVERED\"}"))
                .andExpect(status().isConflict());
    }

    private void whenInvalidTransition() {
        org.mockito.Mockito.doThrow(new InvalidOrderStatusTransitionException(OrderStatus.PREPARING, OrderStatus.DELIVERED))
                .when(manageOrderPort).changeStatus(9, OrderStatus.DELIVERED);
    }

    private OrderCreateRequest validCreateRequest() {
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(2);
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.valueOf(18.50));

        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(1);
        request.setItems(List.of(item));
        return request;
    }

    private Order order() {
        return new Order(
                9,
                1001,
                1,
                BigDecimal.valueOf(18.50),
                OrderStatus.PREPARING,
                LocalDateTime.now(),
                List.of(new OrderItem(2, 1, BigDecimal.valueOf(18.50), BigDecimal.valueOf(18.50)))
        );
    }
}
