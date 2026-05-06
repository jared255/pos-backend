$ErrorActionPreference = 'Stop'

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error 'Docker no esta instalado o no esta disponible en PATH. Usa PostgreSQL nativo y ejecuta .\scripts\init-local-postgres.ps1.'
}

docker compose up -d

Write-Host 'PostgreSQL local solicitado. Verifica estado con: docker compose ps'
