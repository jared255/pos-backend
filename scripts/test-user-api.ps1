$ErrorActionPreference = 'Stop'

$BaseUrl = if ($env:POS_API_BASE_URL) { $env:POS_API_BASE_URL } else { 'http://localhost:8080' }

Write-Host "Probando API de usuarios en $BaseUrl"

Write-Host 'GET /api/roles'
$roles = Invoke-RestMethod "$BaseUrl/api/roles"
$adminRole = $roles | Where-Object { $_.name -eq 'ADMIN' } | Select-Object -First 1
$userRole = $roles | Where-Object { $_.name -eq 'USER' } | Select-Object -First 1

if (-not $adminRole -or -not $userRole) {
    Write-Error 'Se esperaban roles ADMIN y USER. Ejecuta .\scripts\apply-local-migrations.cmd primero.'
}

Write-Host 'POST /api/persons'
$personResponse = Invoke-WebRequest "$BaseUrl/api/persons" `
    -Method Post `
    -ContentType 'application/json' `
    -UseBasicParsing `
    -Body '{
      "firstName": "Ana",
      "paternalLastName": "Perez",
      "maternalLastName": "Rojas",
      "phone": "70000001"
    }'

$personLocation = $personResponse.Headers.Location
$person = Invoke-RestMethod "$BaseUrl$personLocation"
Write-Host "Persona creada en $personLocation"

Write-Host 'POST /api/users admin'
$adminBody = @{
    username = "admin.local.$([DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds())"
    password = 'admin123'
    personId = $person.id
    roleIds = @($adminRole.id)
} | ConvertTo-Json

$userResponse = Invoke-WebRequest "$BaseUrl/api/users" `
    -Method Post `
    -ContentType 'application/json' `
    -UseBasicParsing `
    -Body $adminBody

$userLocation = $userResponse.Headers.Location
$user = Invoke-RestMethod "$BaseUrl$userLocation"

if ($user.roles[0].name -ne 'ADMIN') {
    Write-Error 'El usuario admin no fue creado con rol ADMIN.'
}

if ($user.PSObject.Properties.Name -contains 'password') {
    Write-Error 'La respuesta de usuario no debe exponer password.'
}

Write-Host "Usuario creado en $userLocation"

Write-Host "PUT $userLocation"
$updateBody = @{
    username = $user.username
    password = ''
    personId = $person.id
    roleIds = @($adminRole.id, $userRole.id)
} | ConvertTo-Json

Invoke-WebRequest "$BaseUrl$userLocation" `
    -Method Put `
    -ContentType 'application/json' `
    -UseBasicParsing `
    -Body $updateBody | Out-Null

$updatedUser = Invoke-RestMethod "$BaseUrl$userLocation"
if ($updatedUser.roles.Count -lt 2) {
    Write-Error 'El usuario actualizado debia tener roles ADMIN y USER.'
}

Write-Host "DELETE $userLocation"
Invoke-WebRequest "$BaseUrl$userLocation" -Method Delete -UseBasicParsing | Out-Null

$deletedStatusCode = $null
try {
    Invoke-WebRequest "$BaseUrl$userLocation" -UseBasicParsing | Out-Null
} catch {
    $deletedStatusCode = [int] $_.Exception.Response.StatusCode
}

if ($deletedStatusCode -ne 404) {
    Write-Error "Se esperaba HTTP 404 despues del borrado logico y se recibio $deletedStatusCode"
}

Write-Host 'Pruebas de usuarios completadas.'
