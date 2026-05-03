# Migration Map

## Tabla a tabla

- `category` -> `pos.category`
  Se vuelve dependiente de `store` para soportar sucursales.

- `product` -> `pos.product`
  Se agregan `sku`, `cost`, `track_inventory`, `stock_quantity` y `prep_time_minutes`.

- `order_status` -> `pos.order_status`
  Ahora incluye `code` estable para integraciones y logica de negocio.

- `person` -> `pos.person`
  Se agregan `email`, `document_number` e `updated_at`.

- `role` -> `pos.role`
  Mantiene su funcion y ahora tambien tiene `code`.

- `app_user` -> `pos.app_user`
  Usa `password_hash`, `is_active`, `last_login_at` y auditoria.

- `orders` -> `pos.sales_order`
  Cambia de una tabla minima a una tabla operativa con sucursal, fecha operativa, canal, caja y resumen de cobro.

- `order_detail` -> `pos.sales_order_item`
  Deja de usar PK compuesta `(order_id, product_id)` para permitir varias lineas del mismo producto, notas y snapshot de precio.

- `user_role` -> `pos.user_role`
  Mantiene la relacion many-to-many.

## Cambios de negocio importantes

- La numeracion del pedido ya no depende de un entero libre: ahora se genera por sucursal y fecha.
- El total del pedido ya no queda "a mano": se recalcula con triggers a partir de items y pagos.
- El inventario puede activarse solo para productos que realmente lo necesitan.
- La caja queda representada para soportar aperturas, cierres y conciliacion.

## Cosas que el modelo anterior no cubria bien

- Pagos mixtos o parciales.
- Control de caja por turno.
- Varias lineas del mismo producto en un pedido.
- Escalamiento a mas de una sucursal.
- Snapshot del nombre y precio del producto vendido.
