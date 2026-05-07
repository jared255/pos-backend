param(
    [string] $HostName = 'localhost',
    [int] $Port = 5432,
    [string] $User = 'postgres',
    [string] $Database = 'fastfoodbd',
    [string] $Password = 'postgres',
    [string] $PsqlPath
)

$ErrorActionPreference = 'Stop'

if (-not $PsqlPath) {
    $candidatePaths = @(
        "$env:LOCALAPPDATA\PostgreSQL\18\bin\psql.exe",
        "$env:LOCALAPPDATA\PostgreSQL\17\bin\psql.exe",
        "$env:LOCALAPPDATA\PostgreSQL\16\bin\psql.exe",
        "C:\Program Files\PostgreSQL\18\bin\psql.exe",
        "C:\Program Files\PostgreSQL\17\bin\psql.exe",
        "C:\Program Files\PostgreSQL\16\bin\psql.exe"
    )

    $PsqlPath = $candidatePaths | Where-Object { Test-Path $_ } | Select-Object -First 1
}

if (-not $PsqlPath) {
    Write-Error 'No se encontro psql.exe. Pasa la ruta con -PsqlPath.'
}

$env:PGPASSWORD = $Password

Get-ChildItem db/migrations -Filter *.sql | Sort-Object Name | ForEach-Object {
    Write-Host "Aplicando migracion $($_.Name)"
    & $PsqlPath `
        --host $HostName `
        --port $Port `
        --username $User `
        --dbname $Database `
        --file $_.FullName
}

Write-Host 'Migraciones locales aplicadas.'
