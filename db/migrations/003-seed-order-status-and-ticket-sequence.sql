BEGIN;

INSERT INTO order_status (name) VALUES
    ('PREPARING'),
    ('READY'),
    ('DELIVERED')
ON CONFLICT (name) DO NOTHING;

CREATE SEQUENCE IF NOT EXISTS orders_order_number_seq START WITH 1 INCREMENT BY 1;

SELECT setval(
    'orders_order_number_seq',
    COALESCE((SELECT MAX(order_number) FROM orders), 0),
    true
);

COMMIT;
