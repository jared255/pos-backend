$ErrorActionPreference = 'Stop'

$BaseUrl = if ($env:POS_API_BASE_URL) { $env:POS_API_BASE_URL } else { 'http://localhost:8080' }

Write-Host "Probando API en $BaseUrl"

Write-Host 'GET /api/products'
Invoke-RestMethod "$BaseUrl/api/products"

Write-Host 'GET /api/products/5'
Invoke-RestMethod "$BaseUrl/api/products/5"

Write-Host 'POST /api/products'
$createdResponse = Invoke-WebRequest "$BaseUrl/api/products" `
    -Method Post `
    -ContentType 'application/json' `
    -Body '{
      "name": "Producto local",
      "description": "Producto creado durante pruebas locales.",
      "price": 19.90,
      "stock": 20,
      "status": "ACTIVO",
      "categoryId": 1
    }'

Write-Host "Creado. Location: $($createdResponse.Headers.Location)"

Write-Host 'POST invalido /api/products'
$invalidResponse = Invoke-WebRequest "$BaseUrl/api/products" `
    -Method Post `
    -ContentType 'application/json' `
    -SkipHttpErrorCheck `
    -Body '{
      "name": "",
      "description": "Debe responder 400.",
      "price": -1,
      "stock": -5,
      "status": "ACTIVO"
    }'

if ($invalidResponse.StatusCode -ne 400) {
    Write-Error "Se esperaba HTTP 400 y se recibio $($invalidResponse.StatusCode)"
}

Write-Host 'Pruebas HTTP basicas completadas.'
