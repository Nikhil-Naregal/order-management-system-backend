package com.bnb.app.web.controller;

import com.bnb.app.application.dto.*;
import com.bnb.app.application.service.CustomerOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerOrderService customerOrderService;

    public CustomerController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    @GetMapping
    public List<CustomerResponse> getAll(@RequestParam(required = false) String status,
                                         @RequestParam(required = false) String search) {
        return customerOrderService.getCustomers(status, search);
    }

    @GetMapping("/{id}")
    public CustomerResponse getOne(@PathVariable Long id) {
        return customerOrderService.getCustomer(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        return customerOrderService.create(request);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest request) {
        return customerOrderService.update(id, request);
    }

    @PostMapping("/{id}/order-lines")
    public CustomerResponse addOrderLine(@PathVariable Long id,
                                         @Valid @RequestBody CreateOrderLineRequest request) {
        return customerOrderService.addOrderLine(id, request);
    }

    @DeleteMapping("/{customerId}/order-lines/{lineId}")
    public CustomerResponse removeOrderLine(@PathVariable Long customerId, @PathVariable Long lineId) {
        return customerOrderService.removeOrderLine(customerId, lineId);
    }

    @PutMapping("/{id}/status")
    public CustomerResponse updateStatus(@PathVariable Long id,
                                         @Valid @RequestBody UpdateCustomerStatusRequest request) {
        return customerOrderService.updateStatus(id, request);
    }
}
