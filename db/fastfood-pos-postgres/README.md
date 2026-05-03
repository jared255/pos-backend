# FastFood POS PostgreSQL

Proyecto de base de datos para un POS de fast food sobre PostgreSQL.

## Objetivos

- Separar la base de datos del backend para que el modelo evolucione con menos friccion.
- Pasar de una conversion directa desde MySQL a un esquema pensado para operacion real.
- Cubrir necesidades comunes del negocio: sucursal, caja, pedidos, items, pagos e inventario opcional.
- Aprovechar PostgreSQL con `timestamptz`, `generated columns`, `triggers` y esquema dedicado `pos`.

## Estructura

- `sql/01_schema.sql`: crea el esquema `pos`, tablas, funciones y triggers.
- `sql/02_seed.sql`: carga catalogos y datos base.
- `sql/03_views.sql`: vistas utiles para operacion y reportes.
- `MIGRATION_MAP.md`: mapa entre el modelo anterior y el nuevo.
- `docker-compose.yml`: entorno local rapido con PostgreSQL 16.

## Mejoras clave frente al esquema anterior

- `orders` pasa a `sales_order` y se agrega `store_id`, `business_date`, `channel` y `payment_status`.
- `order_detail` pasa a `sales_order_item` con `id` propio, `line_number` y snapshot de `product_name` y `unit_price`.
- Se soporta numeracion diaria por sucursal con `daily_order_counter`.
- Se agregan `payment_method` y `sales_order_payment` para pagos parciales o mixtos.
- Se agrega `cash_register_session` para apertura y cierre de caja.
- `product` soporta costo, SKU, control de inventario opcional y tiempo estimado de preparacion.
- Todos los registros operativos usan `timestamptz` y timestamps de auditoria.

## Uso local

1. Levantar PostgreSQL:

```bash
docker compose up -d
```

2. Conectarte a la base:

```bash
psql -h localhost -U postgres -d fastfood_pos
```

3. Si quieres reinstalar el esquema sobre una base ya creada:

```sql
\i sql/01_schema.sql
\i sql/02_seed.sql
\i sql/03_views.sql
```

## Notas

- `sql/01_schema.sql` reinicia el esquema `pos` completo. Usalo con cuidado en desarrollo.
- Los datos semilla cargan una sucursal base, catalogos y productos de ejemplo.
- El usuario semilla `admin` usa `CHANGE_ME_HASH` como placeholder: debes reemplazarlo antes de usar autenticacion real.
- Si luego quieres, el siguiente paso natural es conectar este proyecto con Flyway o Liquibase desde el backend.
