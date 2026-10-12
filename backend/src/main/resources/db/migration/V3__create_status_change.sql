CREATE TABLE status_change (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    job_application_id BIGINT      NOT NULL REFERENCES job_application (id) ON DELETE CASCADE,
    from_status        VARCHAR(32) NOT NULL,
    to_status          VARCHAR(32) NOT NULL,
    changed_at         TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_status_change_job_application_id ON status_change (job_application_id);
