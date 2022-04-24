create table "user"
(
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100),
    last_name  VARCHAR(100),
    email      VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "investing_contract"
(
    id                   BIGSERIAL PRIMARY KEY,
    contract_name        VARCHAR(30) NOT NULL,
    currency             VARCHAR(10) NOT NULL     DEFAULT 'mxn',
    annual_interest_rate VARCHAR(15) NOT NULL     DEFAULT '0.00',
    created_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "investing_account"
(
    id              BIGSERIAL PRIMARY KEY,
    contract_id     BIGINT      NOT NULL,
    user_id         BIGINT      NOT NULL,
    status          VARCHAR(20) NOT NULL     DEFAULT 'pending',
    start_balance   VARCHAR(50) NOT NULL     DEFAULT '0.00',
    current_balance VARCHAR(50) NOT NULL     DEFAULT '0.00',
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_contract
        FOREIGN KEY (contract_id)
            REFERENCES investing_contract (id),
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES "user" (id)
);

CREATE TABLE "investing_contract_movement"
(
    id            BIGSERIAL PRIMARY KEY,
    account_id    BIGINT      NOT NULL,
    movement_type VARCHAR(20) NOT NULL     DEFAULT 'initial_balance',
    amount        VARCHAR(50) NOT NULL     DEFAULT '0.00',
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_account
        FOREIGN KEY (account_id)
            REFERENCES investing_account (id)
);
