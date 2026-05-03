BEGIN;

SET search_path TO pos, public;

CREATE OR REPLACE VIEW vw_sales_order_balance AS
SELECT
    so.id,
    s.code AS store_code,
    s.name AS store_name,
    so.business_date,
    so.order_number,
    os.code AS order_status_code,
    os.name AS order_status_name,
    so.channel,
    so.subtotal,
    so.discount_total,
    so.tax_total,
    so.total,
    so.paid_total,
    so.balance_due,
    so.payment_status,
    so.placed_at,
    so.closed_at
FROM sales_order so
JOIN store s ON s.id = so.store_id
JOIN order_status os ON os.id = so.status_id;

CREATE OR REPLACE VIEW vw_daily_sales_summary AS
SELECT
    s.id AS store_id,
    s.code AS store_code,
    s.name AS store_name,
    so.business_date,
    COUNT(*) FILTER (WHERE os.code <> 'CANCELADO') AS completed_orders,
    COUNT(*) FILTER (WHERE os.code = 'CANCELADO') AS cancelled_orders,
    COALESCE(SUM(so.total) FILTER (WHERE os.code <> 'CANCELADO'), 0::numeric) AS gross_sales,
    COALESCE(SUM(so.paid_total) FILTER (WHERE os.code <> 'CANCELADO'), 0::numeric) AS collected_amount
FROM sales_order so
JOIN store s ON s.id = so.store_id
JOIN order_status os ON os.id = so.status_id
GROUP BY s.id, s.code, s.name, so.business_date;

COMMIT;
