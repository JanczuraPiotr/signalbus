CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS console (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP(0)
);
