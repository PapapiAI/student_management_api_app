-- Create role for Flyway (DDL), NOT a superuser
CREATE ROLE flyway_user LOGIN PASSWORD '123456@flyway';

-- App user: only CRUD data
CREATE ROLE app_user LOGIN PASSWORD '123456';

-- Grant database-level permissions
GRANT CONNECT ON DATABASE student_management TO flyway_user, app_user;
GRANT CREATE  ON DATABASE student_management TO flyway_user;  -- để DDL/migrations

-- Work inside the database
\c student_management

-- Create a separate schema for the app, owned by flyway_user
CREATE SCHEMA IF NOT EXISTS app AUTHORIZATION flyway_user;

-- Restrict privileges on the public schema (hardening)
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM PUBLIC;

-- Basic privileges on schema 'app'
GRANT USAGE ON SCHEMA app TO app_user;

-- When flyway_user creates TABLE/SEQUENCE/VIEW/FUNCTION in schema 'app',
-- automatically grant privileges to app_user (to avoid missing GRANT later)
ALTER DEFAULT PRIVILEGES FOR ROLE flyway_user IN SCHEMA app
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES FOR ROLE flyway_user IN SCHEMA app
GRANT USAGE, SELECT ON SEQUENCES TO app_user;
ALTER DEFAULT PRIVILEGES FOR ROLE flyway_user IN SCHEMA app
GRANT EXECUTE ON FUNCTIONS TO app_user;

-- (If using trusted extensions like uuid-ossp or pgcrypto via Flyway)
-- Requires CREATE EXTENSION privilege on DB; if missing, grant it to flyway_user:
GRANT CREATE ON DATABASE student_management TO flyway_user;