SET search_path TO app;

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE SCHEMA IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS students(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(200) UNIQUE NOT NULL,
    age INT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO students(full_name, email, age)
VALUES
('Nguyễn Văn A', 'a@example.com', 20),
('Trần Thị B',  'b@example.com', 22),
('Lê Văn C',    'c@example.com', 21)
ON CONFLICT (email) DO NOTHING;
