CREATE TABLE investing_term
(
    id                 BIGSERIAL PRIMARY KEY,
    calculation_period VARCHAR   NOT NULL,
    created            TIMESTAMP NOT NULL,
    updated            TIMESTAMP
);

CREATE TABLE investing_user_interest
(
    id               BIGSERIAL PRIMARY KEY,
    term_id          BIGINT     NOT NULL,
    user_id          BIGINT     NOT NULL,
    currency         VARCHAR(3) NOT NULL DEFAULT 'mxn',
    start_balance    VARCHAR    NOT NULL DEFAULT '0.00',
    average_balance  VARCHAR    NOT NULL DEFAULT '0.00',
    end_balance      VARCHAR    NOT NULL DEFAULT '0.00',
    interest         VARCHAR    NOT NULL DEFAULT '0.00',
    accrued_interest VARCHAR    NOT NULL DEFAULT '0.00',
    created          TIMESTAMP  NOT NULL,
    updated          TIMESTAMP
);
