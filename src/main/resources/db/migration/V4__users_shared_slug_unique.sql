CREATE UNIQUE INDEX IF NOT EXISTS uq_users_shared_slug_lower
ON users (lower(shared_slug))
WHERE shared_slug IS NOT NULL AND status <> 'DELETADO';
