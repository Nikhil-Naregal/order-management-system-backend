package com.bnb.app.domain.repository;

import com.bnb.app.domain.model.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface OrderLineRepository extends JpaRepository<OrderLineEntity, Long> {
    List<OrderLineEntity> findByCreatedAtBetween(Instant start, Instant end);
}
