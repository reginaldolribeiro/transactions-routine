
package com.example.transactions_routine.repository;

import com.example.transactions_routine.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByDocumentNumber(String documentNumber);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.account.id = :accountId")
    BigDecimal getAccountBalance(@Param("accountId") Long accountId);

    /**
     * Return 1 if updated, 0 if insufficient funds.
     */
    @Modifying
    @Query("""
                UPDATE Account a
                   SET a.balance   = a.balance + :amount,
                       a.updatedAt = CURRENT_TIMESTAMP
                 WHERE a.id        = :accountId
                   AND a.balance + :amount >= 0
            """)
    int updateBalance(Long accountId, BigDecimal amount);

}
