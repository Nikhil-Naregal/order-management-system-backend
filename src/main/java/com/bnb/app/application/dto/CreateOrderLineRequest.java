package com.bnb.app.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderLineRequest(
        @NotNull Long itemId,
        @NotNull @Min(1) Integer quantity
) {
}
