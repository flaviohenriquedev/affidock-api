-- Garante NOT NULL removido (pg_catalog é mais confiável que information_schema em alguns setups).
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM pg_catalog.pg_attribute a
    INNER JOIN pg_catalog.pg_class c ON c.oid = a.attrelid
    INNER JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'public'
      AND c.relname = 'groups'
      AND a.attname = 'icon_slug'
      AND a.attnum > 0
      AND NOT a.attisdropped
      AND a.attnotnull
  ) THEN
    ALTER TABLE public.groups ALTER COLUMN icon_slug DROP NOT NULL;
  END IF;
END $$;
