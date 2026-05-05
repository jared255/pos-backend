# Desarrollo local

El perfil por defecto es `local`, que usa PostgreSQL local en `localhost:5432`.

## Levantar PostgreSQL local

```powershell
docker compose up -d
```

El contenedor crea la base `fastfoodbd` y ejecuta `db/fastfoodbd-postgres.sql` la primera vez que se crea el volumen.

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
