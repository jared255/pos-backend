ALTER TABLE person
    ADD COLUMN IF NOT EXISTS deleted_at timestamp without time zone;

ALTER TABLE role
    ADD COLUMN IF NOT EXISTS deleted_at timestamp without time zone;

ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS deleted_at timestamp without time zone;

CREATE INDEX IF NOT EXISTS idx_person_deleted_at ON person (deleted_at);
CREATE INDEX IF NOT EXISTS idx_role_deleted_at ON role (deleted_at);
CREATE INDEX IF NOT EXISTS idx_app_user_deleted_at ON app_user (deleted_at);

INSERT INTO role (name) VALUES
    ('ADMIN'),
    ('USER')
ON CONFLICT (name) DO NOTHING;
