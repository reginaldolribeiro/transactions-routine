package com.example.transactions_routine.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_type_id", nullable = false)
    private OperationType operationType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // Package-private constructor for Hibernate/JPA
    Transaction() {
    }

    // Private constructor for builder pattern - forces use of builder
    private Transaction(Builder builder) {
        this.id = builder.transactionId;
        this.account = builder.accountId;
        this.operationType = builder.operationTypeId;
        this.amount = builder.amount;
        this.eventDate = builder.eventDate;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        var now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.eventDate == null) {
            this.eventDate = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(account, that.account) &&
               Objects.equals(operationType, that.operationType) &&
               Objects.equals(amount, that.amount) &&
               Objects.equals(eventDate, that.eventDate) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, operationType, amount, eventDate, createdAt, updatedAt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long transactionId;
        private Account accountId;
        private OperationType operationTypeId;
        private BigDecimal amount;
        private LocalDateTime eventDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder transactionId(Long val) {
            transactionId = val;
            return this;
        }

        public Builder account(Account val) {
            accountId = val;
            return this;
        }

        public Builder operationTypeId(OperationType val) {
            operationTypeId = val;
            return this;
        }

        public Builder amount(BigDecimal val) {
            amount = val;
            return this;
        }

        public Builder eventDate(LocalDateTime val) {
            eventDate = val;
            return this;
        }

        public Builder createdAt(LocalDateTime val) {
            createdAt = val;
            return this;
        }

        public Builder updatedAt(LocalDateTime val) {
            updatedAt = val;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }
}
