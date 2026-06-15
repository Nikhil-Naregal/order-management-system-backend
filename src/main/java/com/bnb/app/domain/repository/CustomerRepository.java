package com.bnb.app.domain.repository;

import com.bnb.app.domain.enums.CustomerStatus;
import com.bnb.app.domain.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    List<CustomerEntity> findByStatusOrderByUpdatedAtDesc(CustomerStatus status);
    List<CustomerEntity> findByStatusAndNameContainingIgnoreCaseOrderByUpdatedAtDesc(CustomerStatus status, String name);
    List<CustomerEntity> findAllByOrderByUpdatedAtDesc();
    List<CustomerEntity> findByNameContainingIgnoreCaseOrderByUpdatedAtDesc(String name);
}
