CREATE TABLE investing_term
(
    id                 BIGSERIAL PRIMARY KEY,
    calculation_period VARCHAR   NOT NULL,
    created            TIMESTAMP NOT NULL DEFAULT now(),
    updated            TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE investing_user_interest
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    currency         VARCHAR(10) NOT NULL DEFAULT 'mxn',
    start_balance    VARCHAR     NOT NULL DEFAULT '0.00',
    average_balance  VARCHAR     NOT NULL DEFAULT '0.00',
    end_balance      VARCHAR     NOT NULL DEFAULT '0.00',
    interest         VARCHAR     NOT NULL DEFAULT '0.00',
    accrued_interest VARCHAR     NOT NULL DEFAULT '0.00',
    created          TIMESTAMP   NOT NULL DEFAULT now(),
    updated          TIMESTAMP   NOT NULL DEFAULT now()
);
