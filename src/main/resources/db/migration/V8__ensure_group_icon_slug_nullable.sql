-- Idempotente: corrige bancos onde icon_slug ainda está NOT NULL (ex.: V7 não aplicada).
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'groups'
      AND column_name = 'icon_slug'
      AND is_nullable = 'NO'
  ) THEN
    ALTER TABLE groups ALTER COLUMN icon_slug DROP NOT NULL;
  END IF;
END $$;
