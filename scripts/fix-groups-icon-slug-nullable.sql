-- Correção manual se a coluna ainda estiver NOT NULL apesar das migrações V7–V9
-- (histórico Flyway inconsistente, baseline, ou migração ignorada).
-- PostgreSQL: se já estiver nullable, o comando abaixo pode falhar com
-- "column ... is not marked NOT NULL" — nesse caso pode ignorar.
ALTER TABLE public.groups ALTER COLUMN icon_slug DROP NOT NULL;
