package com.bnb.app.application.mapper;

import com.bnb.app.application.dto.*;
import com.bnb.app.application.strategy.BillCalculator;
import com.bnb.app.domain.model.CategoryEntity;
import com.bnb.app.domain.model.CustomerEntity;
import com.bnb.app.domain.model.ItemEntity;
import com.bnb.app.domain.model.OrderLineEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BnbMapper {

    private final BillCalculator billCalculator;

    public BnbMapper(BillCalculator billCalculator) {
        this.billCalculator = billCalculator;
    }

    public CategoryResponse toCategoryResponse(CategoryEntity category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getItems().size());
    }

    public CategorySlimResponse toCategorySlimResponse(CategoryEntity category) {
        return new CategorySlimResponse(category.getId(), category.getName());
    }

    public ItemResponse toItemResponse(ItemEntity item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.isInStock(),
                toCategorySlimResponse(item.getCategory())
        );
    }

    public OrderLineResponse toOrderLineResponse(OrderLineEntity line) {
        BigDecimal total = line.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(line.getQuantity()));
        return new OrderLineResponse(
                line.getId(),
                line.getItem().getId(),
                line.getItemNameSnapshot(),
                line.getCategoryNameSnapshot(),
                line.getUnitPriceSnapshot(),
                line.getQuantity(),
                total
        );
    }

    public CustomerResponse toCustomerResponse(CustomerEntity customer) {
        List<OrderLineResponse> lines = customer.getOrderLines().stream()
                .map(this::toOrderLineResponse)
                .toList();

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getStatus().name(),
                billCalculator.calculate(customer.getOrderLines()),
                lines,
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
