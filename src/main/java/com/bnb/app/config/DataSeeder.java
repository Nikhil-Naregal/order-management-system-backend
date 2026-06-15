package com.bnb.app.config;

import com.bnb.app.application.service.OrderLineFactory;
import com.bnb.app.domain.enums.CustomerStatus;
import com.bnb.app.domain.model.CategoryEntity;
import com.bnb.app.domain.model.CustomerEntity;
import com.bnb.app.domain.model.ItemEntity;
import com.bnb.app.domain.repository.CategoryRepository;
import com.bnb.app.domain.repository.CustomerRepository;
import com.bnb.app.domain.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(CategoryRepository categoryRepository,
                               ItemRepository itemRepository,
                               CustomerRepository customerRepository,
                               OrderLineFactory orderLineFactory) {
        return args -> {
            if (categoryRepository.count() == 0) {
                CategoryEntity burgers = categoryRepository.save(new CategoryEntity("Burgers"));
                CategoryEntity beverages = categoryRepository.save(new CategoryEntity("Beverages"));
                CategoryEntity sides = categoryRepository.save(new CategoryEntity("Sides"));

                ItemEntity classic = itemRepository.save(new ItemEntity("Classic Smash Burger", new BigDecimal("199.00"), true, burgers));
                ItemEntity cheese = itemRepository.save(new ItemEntity("Double Cheese Burger", new BigDecimal("249.00"), true, burgers));
                ItemEntity cola = itemRepository.save(new ItemEntity("Craft Cola", new BigDecimal("79.00"), true, beverages));
                ItemEntity coldCoffee = itemRepository.save(new ItemEntity("Cold Brew Float", new BigDecimal("129.00"), true, beverages));
                itemRepository.save(new ItemEntity("Loaded Fries", new BigDecimal("149.00"), true, sides));
                itemRepository.save(new ItemEntity("Orange Fizz", new BigDecimal("99.00"), false, beverages));

                if (customerRepository.count() == 0) {
                    CustomerEntity arya = new CustomerEntity("Arya Blaze", CustomerStatus.ACTIVE);
                    arya.addOrderLine(orderLineFactory.create(classic, 1));
                    arya.addOrderLine(orderLineFactory.create(cola, 2));

                    CustomerEntity zane = new CustomerEntity("Zane Pepper", CustomerStatus.ACTIVE);
                    zane.addOrderLine(orderLineFactory.create(cheese, 1));
                    zane.addOrderLine(orderLineFactory.create(coldCoffee, 1));

                    CustomerEntity maya = new CustomerEntity("Maya Frost", CustomerStatus.INACTIVE);
                    maya.addOrderLine(orderLineFactory.create(classic, 1));

                    customerRepository.saveAll(List.of(arya, zane, maya));
                }
            }
        };
    }
}
