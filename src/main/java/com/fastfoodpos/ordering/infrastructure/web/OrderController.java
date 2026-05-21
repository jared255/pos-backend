package com.fastfoodpos.ordering.infrastructure.web;

import com.fastfoodpos.ordering.domain.model.OrderStatus;
import com.fastfoodpos.ordering.domain.port.in.ManageOrderPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final ManageOrderPort manageOrderPort;

    public OrderController(ManageOrderPort manageOrderPort) {
        this.manageOrderPort = manageOrderPort;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody OrderCreateRequest request) {
        Integer id = manageOrderPort.createOrder(request.toDomain());
        return ResponseEntity.created(URI.create("/api/orders/" + id)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Integer id) {
        return manageOrderPort.findById(id)
                .map(OrderResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<OrderResponse> findByStatus(@RequestParam(name = "status") List<OrderStatus> statuses) {
        return manageOrderPort.findByStatuses(statuses)
                .stream()
                .map(OrderResponse::fromDomain)
                .toList();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Integer id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        manageOrderPort.changeStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }
}
