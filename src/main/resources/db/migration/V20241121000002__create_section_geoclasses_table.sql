CREATE TABLE section_geoclass (
    section_id  BIGINT NOT NULL,
    geoclass_id BIGINT NOT NULL,
    PRIMARY KEY (section_id, geoclass_id),
    FOREIGN KEY (section_id) REFERENCES sections (id) ON DELETE CASCADE,
    FOREIGN KEY (geoclass_id) REFERENCES geoclasses (id) ON DELETE CASCADE
);