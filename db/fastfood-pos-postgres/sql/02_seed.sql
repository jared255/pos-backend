BEGIN;

SET search_path TO pos, public;

INSERT INTO store (code, name, phone, timezone_name)
VALUES ('LPZ-01', 'FastFood Central', '77777777', 'America/La_Paz');

INSERT INTO role (code, name) VALUES
    ('ADMIN', 'Administrador'),
    ('CAJERO', 'Cajero'),
    ('COCINA', 'Cocina');

INSERT INTO order_status (code, name) VALUES
    ('PENDIENTE', 'Pendiente'),
    ('EN_PREPARACION', 'En preparacion'),
    ('LISTO', 'Listo para entrega'),
    ('ENTREGADO', 'Entregado'),
    ('CANCELADO', 'Cancelado');

INSERT INTO payment_method (code, name, requires_reference) VALUES
    ('EFECTIVO', 'Efectivo', false),
    ('TARJETA', 'Tarjeta', false),
    ('QR', 'QR', true),
    ('TRANSFERENCIA', 'Transferencia', true);

INSERT INTO person (first_name, paternal_last_name, maternal_last_name, phone, email, document_number)
VALUES ('Admin', 'POS', 'FastFood', '70000000', 'admin@fastfood.local', 'ADMIN-001');

INSERT INTO app_user (username, password_hash, person_id)
VALUES ('admin', 'CHANGE_ME_HASH', (SELECT id FROM person WHERE document_number = 'ADMIN-001'));

INSERT INTO user_role (user_id, role_id)
SELECT au.id, r.id
FROM app_user au
JOIN role r ON r.code = 'ADMIN'
WHERE au.username = 'admin';

INSERT INTO category (store_id, name, description, display_order)
VALUES
    ((SELECT id FROM store WHERE code = 'LPZ-01'), 'Refrescos', 'Bebidas frias y embotelladas.', 1),
    ((SELECT id FROM store WHERE code = 'LPZ-01'), 'Hamburguesas', 'Linea principal de hamburguesas y combos.', 2),
    ((SELECT id FROM store WHERE code = 'LPZ-01'), 'Complementos', 'Papas, nuggets y acompanamientos.', 3);

INSERT INTO product (
    store_id,
    category_id,
    sku,
    name,
    description,
    price,
    cost,
    track_inventory,
    stock_quantity,
    prep_time_minutes,
    status
) VALUES
    (
        (SELECT id FROM store WHERE code = 'LPZ-01'),
        (SELECT id FROM category WHERE name = 'Refrescos' AND store_id = (SELECT id FROM store WHERE code = 'LPZ-01')),
        'BEB-COCA-355',
        'Coca-Cola',
        'Refresco de cola de 355ml.',
        18.50,
        9.00,
        true,
        100,
        0,
        'ACTIVO'
    ),
    (
        (SELECT id FROM store WHERE code = 'LPZ-01'),
        (SELECT id FROM category WHERE name = 'Refrescos' AND store_id = (SELECT id FROM store WHERE code = 'LPZ-01')),
        'BEB-SPRITE-355',
        'Sprite',
        'Refresco de limon de 355ml.',
        17.50,
        8.50,
        true,
        50,
        0,
        'AGOTADO'
    ),
    (
        (SELECT id FROM store WHERE code = 'LPZ-01'),
        (SELECT id FROM category WHERE name = 'Hamburguesas' AND store_id = (SELECT id FROM store WHERE code = 'LPZ-01')),
        'HAM-CLASICA',
        'Hamburguesa Clasica',
        'Hamburguesa con queso, lechuga y salsa especial.',
        29.90,
        14.50,
        false,
        NULL,
        8,
        'ACTIVO'
    ),
    (
        (SELECT id FROM store WHERE code = 'LPZ-01'),
        (SELECT id FROM category WHERE name = 'Complementos' AND store_id = (SELECT id FROM store WHERE code = 'LPZ-01')),
        'COMP-PAPAS-M',
        'Papas Medianas',
        'Papas fritas porcion mediana.',
        12.00,
        4.20,
        false,
        NULL,
        4,
        'ACTIVO'
    );

COMMIT;
