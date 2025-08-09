package com.example.transactions_routine.repository;

import com.example.transactions_routine.model.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {
    Optional<OperationType> findByDescription(String description);
}
