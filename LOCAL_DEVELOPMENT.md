# Desarrollo local

El perfil por defecto es `local`, que usa PostgreSQL local en `localhost:5432`.

## Opcion A: levantar PostgreSQL local con Docker

```powershell
docker compose up -d
```

El contenedor crea la base `fastfoodbd` y ejecuta `db/fastfoodbd-postgres.sql` la primera vez que se crea el volumen.

Tambien puedes usar el script:

```powershell
.\scripts\start-local-db.ps1
```

## Opcion B: usar PostgreSQL nativo

Si Docker Desktop esta bloqueado por la maquina o el dominio, instala PostgreSQL nativo y asegúrate de que `psql` este disponible en `PATH`.

La configuracion local espera:

```text
host: localhost
port: 5432
database: fastfoodbd
user: postgres
password: postgres
```

Para crear la base y cargar el esquema:

```powershell
.\scripts\init-local-postgres.ps1
```

Si tu password local no es `postgres`:

```powershell
.\scripts\init-local-postgres.ps1 -Password 'tu-password-local'
```

## Ejecutar la API

```powershell
$env:JAVA_HOME='C:\Users\lluizagac\.jdks\temurin-21.0.11'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat bootRun
```

## Usar Supabase

```powershell
$env:DB_PASS='tu-password-de-supabase'
.\gradlew.bat bootRun --args='--spring.profiles.active=supabase'
```

## Probar endpoints

```powershell
Invoke-RestMethod http://localhost:8080/api/products
```

Tambien puedes ejecutar pruebas HTTP basicas:

```powershell
.\scripts\test-local-api.ps1
```

Si usas IntelliJ IDEA, abre `http/product-api.http` y ejecuta las solicitudes desde el editor.
