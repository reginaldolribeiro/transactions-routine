CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    document_number VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE operation_types (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    credit BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    operation_type_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    idempotency_key UUID NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_transactions_account_id FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT fk_transactions_operation_type_id FOREIGN KEY (operation_type_id) REFERENCES operation_types(id)
);

CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_operation_type_id ON transactions(operation_type_id);
CREATE INDEX idx_transactions_account_event_date ON transactions(account_id, event_date);
CREATE UNIQUE INDEX idx_transactions_account_idempotency_key ON transactions(account_id, idempotency_key);

-- initial data
INSERT INTO accounts (document_number, created_at, updated_at) VALUES
('12345678900', '2020-01-01T08:00:00.0000000', '2020-01-01T08:00:00.0000000');

INSERT INTO operation_types (description, credit, created_at, updated_at) VALUES
('Normal Purchase', FALSE, '2020-01-01T08:00:00.0000000', '2020-01-01T08:00:00.0000000'),
('Purchase with installments', FALSE, '2020-01-01T08:00:00.0000000', '2020-01-01T08:00:00.0000000'),
('Withdrawal', FALSE, '2020-01-01T08:00:00.0000000', '2020-01-01T08:00:00.0000000'),
('Credit Voucher', TRUE, '2020-01-01T08:00:00.0000000', '2020-01-01T08:00:00.0000000');

INSERT INTO transactions (account_id, operation_type_id, amount, event_date, idempotency_key, created_at, updated_at) VALUES
(1, 1, -50.0, '2020-01-01T10:32:07.7199222', 'a1b2c3d4-e5f6-7890-abcd-123456789001', '2020-01-01T10:32:07.7199222', '2020-01-01T10:32:07.7199222'),
(1, 1, -23.5, '2020-01-01T10:48:12.2135875', 'a1b2c3d4-e5f6-7890-abcd-123456789002', '2020-01-01T10:48:12.2135875', '2020-01-01T10:48:12.2135875'),
(1, 1, -18.7, '2020-01-02T19:01:23.1458543', 'a1b2c3d4-e5f6-7890-abcd-123456789003', '2020-01-02T19:01:23.1458543', '2020-01-02T19:01:23.1458543'),
(1, 4, 60.0, '2020-01-05T09:34:18.5893223', 'a1b2c3d4-e5f6-7890-abcd-123456789004', '2020-01-05T09:34:18.5893223', '2020-01-05T09:34:18.5893223');
