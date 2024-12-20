CREATE UNIQUE INDEX unique_import_in_progress
ON jobs (type, status)
WHERE type = 'IMPORT' AND status = 'IN_PROGRESS';