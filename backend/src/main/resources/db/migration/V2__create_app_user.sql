CREATE TABLE app_user (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

ALTER TABLE job_application
    ADD COLUMN user_id BIGINT NOT NULL REFERENCES app_user (id);

CREATE INDEX idx_job_application_user_id ON job_application (user_id);
