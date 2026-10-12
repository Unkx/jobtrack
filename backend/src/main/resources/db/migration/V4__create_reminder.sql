CREATE TABLE reminder (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    job_application_id BIGINT      NOT NULL UNIQUE REFERENCES job_application (id) ON DELETE CASCADE,
    created_at         TIMESTAMPTZ NOT NULL,
    read_at            TIMESTAMPTZ
);
