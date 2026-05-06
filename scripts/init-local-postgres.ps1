param(
    [string] $HostName = 'localhost',
    [int] $Port = 5432,
    [string] $User = 'postgres',
    [string] $Database = 'fastfoodbd',
    [string] $Password = 'postgres'
)

$ErrorActionPreference = 'Stop'

if (-not (Get-Command psql -ErrorAction SilentlyContinue)) {
    Write-Error 'psql no esta instalado o no esta disponible en PATH. Instala PostgreSQL nativo antes de ejecutar este script.'
}

$env:PGPASSWORD = $Password

$databaseExists = psql `
    --host $HostName `
    --port $Port `
    --username $User `
    --dbname postgres `
    --tuples-only `
    --command "SELECT 1 FROM pg_database WHERE datname = '$Database';"

if (-not $databaseExists.Trim()) {
    Write-Host "Creando base de datos $Database"
    psql `
        --host $HostName `
        --port $Port `
        --username $User `
        --dbname postgres `
        --command "CREATE DATABASE $Database;"
}

Write-Host "Inicializando esquema en $Database"
psql `
    --host $HostName `
    --port $Port `
    --username $User `
    --dbname $Database `
    --file db/fastfoodbd-postgres.sql

Write-Host 'PostgreSQL local inicializado.'
