CREATE TABLE geoclasses (
    id BIGSERIAL NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);