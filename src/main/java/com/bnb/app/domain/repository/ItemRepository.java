package com.bnb.app.domain.repository;

import com.bnb.app.domain.model.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findAllByOrderByNameAsc();
    List<ItemEntity> findByInStockOrderByNameAsc(boolean inStock);
}
