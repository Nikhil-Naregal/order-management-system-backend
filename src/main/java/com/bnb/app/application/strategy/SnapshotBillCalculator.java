package com.bnb.app.application.strategy;

import com.bnb.app.domain.model.OrderLineEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SnapshotBillCalculator implements BillCalculator {

    @Override
    public BigDecimal calculate(List<OrderLineEntity> orderLines) {
        return orderLines.stream()
                .map(line -> line.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(line.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
