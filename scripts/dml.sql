-- Substitute REDACTED to our key
INSERT INTO users (staff_id, username, admin, password) VALUES (1, 'admin', 1, AES_ENCRYPT('admin', 'REDACTED'));

INSERT INTO users (staff_id, username, admin, password) VALUES (11, 'octavian', 1, AES_ENCRYPT('roman', 'REDACTED'));