package com.bnb.app.application.strategy;

import com.bnb.app.domain.model.OrderLineEntity;

import java.math.BigDecimal;
import java.util.List;

public interface BillCalculator {
    BigDecimal calculate(List<OrderLineEntity> orderLines);
}
