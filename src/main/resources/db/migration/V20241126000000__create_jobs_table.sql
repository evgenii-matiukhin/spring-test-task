CREATE TABLE jobs (
    id UUID PRIMARY KEY,
    type VARCHAR(255) NOT NULL CHECK (type IN ('IMPORT', 'EXPORT')),
    status VARCHAR(255) NOT NULL CHECK (status IN ('DONE', 'IN_PROGRESS', 'ERROR')),
    started_at TIMESTAMP,
    finished_at TIMESTAMP
);