ALTER TABLE users
    ADD COLUMN IF NOT EXISTS avatar_file_id UUID,
    ADD COLUMN IF NOT EXISTS shared_slug VARCHAR(120),
    ADD COLUMN IF NOT EXISTS phone VARCHAR(30),
    ADD COLUMN IF NOT EXISTS whatsapp VARCHAR(30),
    ADD COLUMN IF NOT EXISTS secondary_email VARCHAR(160),
    ADD COLUMN IF NOT EXISTS linkedin_url VARCHAR(300),
    ADD COLUMN IF NOT EXISTS website_url VARCHAR(300),
    ADD COLUMN IF NOT EXISTS bio VARCHAR(500),
    ADD COLUMN IF NOT EXISTS theme_preference VARCHAR(20);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_users_avatar_file'
          AND table_name = 'users'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT fk_users_avatar_file
            FOREIGN KEY (avatar_file_id) REFERENCES file_assets(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_users_avatar_file_id ON users(avatar_file_id);
CREATE INDEX IF NOT EXISTS idx_users_shared_slug ON users(shared_slug);
