package com.bnb.app.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerRequest(@NotBlank String name) {
}
