package com.example.transactions_routine.repository;

import com.example.transactions_routine.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdempotencyKey(UUID idempotencyKey);
    
    // Trade-off analysis for idempotency check query:
    // Current approach: Simple query + lazy loading (1 + 2 queries = 3 total)
    // Alternative: JOIN FETCH approach (1 complex query)
    // 
    // Pros of current approach:
    // - Simple query is faster for the common case (new transactions)
    // - Less memory usage when idempotency check fails
    // - Easier to maintain and debug
    //
    // Pros of JOIN FETCH:
    // - Single query eliminates N+1 problem for duplicate transactions
    // - Better performance when idempotency key already exists
    //
    // Decision: Keep simple approach since idempotency hits are rare in practice
    // If needed for optimization: @Query("SELECT t FROM Transaction t JOIN FETCH t.account JOIN FETCH t.operationType WHERE t.account.id = :accountId AND t.idempotencyKey = :idempotencyKey")
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.idempotencyKey = :idempotencyKey")
    Optional<Transaction> findByAccountIdAndIdempotencyKey(@Param("accountId") Long accountId, @Param("idempotencyKey") UUID idempotencyKey);
    
    // Lightweight existence check for idempotency - optimized for common case (no duplicates)
    // Alternative approach: using COUNT only
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.id = :accountId AND t.idempotencyKey = :idempotencyKey")
    long countByAccountIdAndIdempotencyKey(@Param("accountId") Long accountId, @Param("idempotencyKey") UUID idempotencyKey);
    
    // Keep the original for comparison
//    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Transaction t WHERE t.account.id = :accountId AND t.idempotencyKey = :idempotencyKey")
//    boolean existsByAccountIdAndIdempotencyKey(@Param("accountId") Long accountId, @Param("idempotencyKey") UUID idempotencyKey);
//
//    // Using native SQL to avoid Spring Data JPA proxy/cache issues that cause duplicate queries
//    @Query(value = "SELECT COUNT(*) FROM transactions WHERE account_id = :accountId AND idempotency_key = :idempotencyKey", nativeQuery = true)
//    long countByAccountIdAndIdempotencyKeyNative(@Param("accountId") Long accountId, @Param("idempotencyKey") UUID idempotencyKey);
}

