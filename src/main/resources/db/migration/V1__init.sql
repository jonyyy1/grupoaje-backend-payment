CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS payment (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id           VARCHAR(64) NOT NULL,
    request_hash         VARCHAR(64) NOT NULL,
    user_id              VARCHAR(64) NOT NULL,
    merchant_id          VARCHAR(64) NOT NULL,
    currency             VARCHAR(3) NOT NULL,
    amount               NUMERIC(19,2) NOT NULL,
    status               VARCHAR(16) NOT NULL,
    error_code           VARCHAR(64),
    user_balance_after   NUMERIC(19,2),
    merchant_balance_after NUMERIC(19,2),
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ NOT NULL,

    CONSTRAINT uq_payment_payment_id UNIQUE (payment_id)
    );

CREATE TABLE IF NOT EXISTS account_balance (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_type  VARCHAR(16) NOT NULL,
    owner_id    VARCHAR(64) NOT NULL,
    currency    VARCHAR(3) NOT NULL,
    balance     NUMERIC(19,2) NOT NULL,
    version     BIGINT NOT NULL DEFAULT 0,
    updated_at  TIMESTAMPTZ NOT NULL,

    CONSTRAINT uq_balance_owner_currency UNIQUE (owner_type, owner_id, currency),
    CONSTRAINT ck_balance_non_negative CHECK (balance >= 0)
    );

CREATE INDEX IF NOT EXISTS idx_payment_user ON payment(user_id);
CREATE INDEX IF NOT EXISTS idx_balance_owner ON account_balance(owner_type, owner_id);
