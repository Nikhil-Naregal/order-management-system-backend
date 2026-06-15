package com.bnb.app.application.service;

import com.bnb.app.application.dto.*;
import com.bnb.app.application.mapper.BnbMapper;
import com.bnb.app.domain.enums.CustomerStatus;
import com.bnb.app.domain.model.CustomerEntity;
import com.bnb.app.domain.model.ItemEntity;
import com.bnb.app.domain.model.OrderLineEntity;
import com.bnb.app.domain.repository.CustomerRepository;
import com.bnb.app.domain.repository.ItemRepository;
import com.bnb.app.web.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerOrderService {

    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final BnbMapper mapper;
    private final OrderLineFactory orderLineFactory;

    public CustomerOrderService(CustomerRepository customerRepository,
                                ItemRepository itemRepository,
                                BnbMapper mapper,
                                OrderLineFactory orderLineFactory) {
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.orderLineFactory = orderLineFactory;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomers(String statusValue, String search) {
        List<CustomerEntity> customers;
        String normalizedSearch = search == null ? "" : search.trim();

        if (statusValue == null || statusValue.isBlank()) {
            customers = normalizedSearch.isBlank()
                    ? customerRepository.findAllByOrderByUpdatedAtDesc()
                    : customerRepository.findByNameContainingIgnoreCaseOrderByUpdatedAtDesc(normalizedSearch);
        } else {
            CustomerStatus status = parseStatus(statusValue);
            customers = normalizedSearch.isBlank()
                    ? customerRepository.findByStatusOrderByUpdatedAtDesc(status)
                    : customerRepository.findByStatusAndNameContainingIgnoreCaseOrderByUpdatedAtDesc(status, normalizedSearch);
        }

        return customers.stream().map(mapper::toCustomerResponse).toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long id) {
        return mapper.toCustomerResponse(findCustomer(id));
    }

    public CustomerResponse create(CreateCustomerRequest request) {
        CustomerEntity customer = new CustomerEntity(request.name().trim(), CustomerStatus.ACTIVE);
        if (request.orderLines() != null) {
            request.orderLines().forEach(lineRequest -> addLine(customer, lineRequest));
        }
        CustomerEntity saved = customerRepository.save(customer);
        return mapper.toCustomerResponse(saved);
    }

    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        CustomerEntity customer = findCustomer(id);
        customer.setName(request.name().trim());
        return mapper.toCustomerResponse(customer);
    }

    public CustomerResponse addOrderLine(Long customerId, CreateOrderLineRequest request) {
        CustomerEntity customer = findCustomer(customerId);
        addLine(customer, request);
        return mapper.toCustomerResponse(customer);
    }

    public CustomerResponse removeOrderLine(Long customerId, Long lineId) {
        CustomerEntity customer = findCustomer(customerId);
        OrderLineEntity line = customer.getOrderLines().stream()
                .filter(existing -> existing.getId().equals(lineId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Order line not found: " + lineId));
        customer.removeOrderLine(line);
        return mapper.toCustomerResponse(customer);
    }

    public CustomerResponse updateStatus(Long id, UpdateCustomerStatusRequest request) {
        CustomerEntity customer = findCustomer(id);
        customer.setStatus(parseStatus(request.status()));
        return mapper.toCustomerResponse(customer);
    }

    private void addLine(CustomerEntity customer, CreateOrderLineRequest request) {
        ItemEntity item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + request.itemId()));

        if (!item.isInStock()) {
            throw new IllegalArgumentException("Item is out of stock: " + item.getName());
        }

        customer.addOrderLine(orderLineFactory.create(item, request.quantity()));
    }

    private CustomerEntity findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    private CustomerStatus parseStatus(String rawStatus) {
        try {
            return CustomerStatus.valueOf(rawStatus.trim().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unsupported status: " + rawStatus);
        }
    }
}
