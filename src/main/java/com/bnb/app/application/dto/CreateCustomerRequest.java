package com.bnb.app.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateCustomerRequest(
        @NotBlank String name,
        List<CreateOrderLineRequest> orderLines
) {
}
