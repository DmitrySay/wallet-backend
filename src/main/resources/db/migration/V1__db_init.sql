create table wallets
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(255),
    balance     NUMERIC(12, 2) DEFAULT 0.00 CHECK (balance >= 0)
);
