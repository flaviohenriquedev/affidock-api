ALTER TABLE products
    ADD COLUMN IF NOT EXISTS image_url VARCHAR(1200),
    ADD COLUMN IF NOT EXISTS producer_name VARCHAR(180),
    ADD COLUMN IF NOT EXISTS original_price_cents BIGINT,
    ADD COLUMN IF NOT EXISTS sale_price_cents BIGINT;

UPDATE products
SET sale_price_cents = 0
WHERE sale_price_cents IS NULL;
