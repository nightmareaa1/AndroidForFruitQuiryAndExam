ALTER TABLE users
    ADD COLUMN initial_password VARCHAR(255) NULL AFTER password_hash;
