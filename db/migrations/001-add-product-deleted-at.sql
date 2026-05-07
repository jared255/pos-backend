ALTER TABLE product
    ADD COLUMN IF NOT EXISTS deleted_at timestamp without time zone;

CREATE INDEX IF NOT EXISTS idx_product_deleted_at ON product (deleted_at);
