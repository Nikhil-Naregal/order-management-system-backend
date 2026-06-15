package com.bnb.app.application.service;

import com.bnb.app.domain.model.ItemEntity;
import com.bnb.app.domain.model.OrderLineEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderLineFactory {

    public OrderLineEntity create(ItemEntity item, Integer quantity) {
        return new OrderLineEntity(
                item,
                item.getName(),
                item.getCategory().getName(),
                item.getPrice(),
                quantity
        );
    }
}
