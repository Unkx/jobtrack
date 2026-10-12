CREATE TABLE job_application (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company    VARCHAR(255)  NOT NULL,
    position   VARCHAR(255)  NOT NULL,
    offer_url  VARCHAR(2048),
    salary_min INTEGER,
    salary_max INTEGER,
    source     VARCHAR(32)   NOT NULL,
    applied_at DATE          NOT NULL,
    status     VARCHAR(32)   NOT NULL,
    notes      TEXT
);
