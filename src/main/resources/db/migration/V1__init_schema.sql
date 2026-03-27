CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(120) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    google_subject VARCHAR(160),
    provider VARCHAR(20) NOT NULL,
    avatar_url VARCHAR(600)
);

CREATE TABLE IF NOT EXISTS groups (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(120) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    name VARCHAR(140) NOT NULL,
    brand_hex VARCHAR(7) NOT NULL,
    icon_slug VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(120) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    group_id UUID NOT NULL REFERENCES groups(id),
    name VARCHAR(180) NOT NULL,
    accent_hex VARCHAR(7) NOT NULL,
    affiliate_url VARCHAR(800) NOT NULL
);

CREATE TABLE IF NOT EXISTS file_assets (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(120) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    storage_provider VARCHAR(40) NOT NULL,
    object_key VARCHAR(240) NOT NULL,
    public_url VARCHAR(800) NOT NULL,
    mime_type VARCHAR(120) NOT NULL,
    size_bytes BIGINT NOT NULL,
    original_name VARCHAR(240) NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(120) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    token_id VARCHAR(120) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_groups_status ON groups(status);
CREATE INDEX IF NOT EXISTS idx_products_group_id ON products(group_id);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
