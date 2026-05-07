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
$createdLocation = $createdResponse.Headers.Location
$createdProduct = Invoke-RestMethod "$BaseUrl$createdLocation"

Write-Host "GET $createdLocation"
if ($createdProduct.name -ne 'Producto local') {
    Write-Error "El producto creado no fue devuelto correctamente desde $createdLocation"
}

Write-Host "PUT $createdLocation"
Invoke-WebRequest "$BaseUrl$createdLocation" `
    -Method Put `
    -ContentType 'application/json' `
    -Body '{
      "name": "Producto local actualizado",
      "description": "Producto actualizado durante pruebas locales.",
      "price": 21.50,
      "stock": 15,
      "status": "ACTIVO",
      "categoryId": 1
    }' | Out-Null

Write-Host "DELETE $createdLocation"
Invoke-WebRequest "$BaseUrl$createdLocation" -Method Delete | Out-Null

Write-Host "GET borrado $createdLocation"
$deletedResponse = Invoke-WebRequest "$BaseUrl$createdLocation" -SkipHttpErrorCheck
if ($deletedResponse.StatusCode -ne 404) {
    Write-Error "Se esperaba HTTP 404 despues del borrado logico y se recibio $($deletedResponse.StatusCode)"
}

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
