CREATE TABLE note (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    job_application_id BIGINT      NOT NULL REFERENCES job_application (id) ON DELETE CASCADE,
    content            TEXT        NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_note_job_application_id ON note (job_application_id);

-- Carry over the single free-text field each application had until now
INSERT INTO note (job_application_id, content, created_at)
SELECT id, notes, now() FROM job_application WHERE notes IS NOT NULL AND btrim(notes) <> '';

ALTER TABLE job_application DROP COLUMN notes;
