package com.bnb.app.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerStatusRequest(@NotBlank String status) {
}
