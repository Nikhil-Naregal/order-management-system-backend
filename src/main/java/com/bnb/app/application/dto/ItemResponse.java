package com.bnb.app.application.dto;

import java.math.BigDecimal;

public record ItemResponse(Long id, String name, BigDecimal price, boolean inStock, CategorySlimResponse category) {
}
