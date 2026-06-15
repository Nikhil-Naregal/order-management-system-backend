package com.bnb.app.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CustomerResponse(
        Long id,
        String name,
        String status,
        BigDecimal totalBill,
        List<OrderLineResponse> orderLines,
        Instant createdAt,
        Instant updatedAt
) {
}
