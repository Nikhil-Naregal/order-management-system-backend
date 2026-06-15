package com.bnb.app.application.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long id,
        Long itemId,
        String itemName,
        String categoryName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal lineTotal
) {
}
