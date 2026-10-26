# Author: Phan Tuan Thanh
[CmdletBinding()]
param(
    [ValidateSet('h2', 'sqlserver')]
    [string]$Profile = 'h2'
)

$ErrorActionPreference = 'Stop'
if (-not (Get-Command java -ErrorAction SilentlyContinue)) { throw 'Install JDK 21 and add java to PATH.' }
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) { throw 'Install Maven 3.9+ and add mvn to PATH.' }
if ($Profile -eq 'sqlserver' -and [string]::IsNullOrWhiteSpace($env:LIBRARY_DB_PASSWORD)) {
    throw 'Run slot 29/sql/setup-sqlserver.sql in SSMS, then set LIBRARY_DB_URL, LIBRARY_DB_USER and LIBRARY_DB_PASSWORD. See README.md.'
}

Push-Location (Join-Path $PSScriptRoot 'slot 29')
try {
    & mvn -B -ntp clean package
    if ($LASTEXITCODE -ne 0) { throw 'Maven build/test failed. See output above.' }
    $activeProfiles = if ($Profile -eq 'sqlserver') { 'sqlserver,demo' } else { 'demo' }
    Write-Host 'Open http://localhost:8080 after startup. Press Ctrl+C to stop.'
    & java -jar target/slot29-library-hub-1.0.0.jar "--spring.profiles.active=$activeProfiles"
    if ($LASTEXITCODE -ne 0) { throw 'Application failed. See output above.' }
} finally {
    Pop-Location
}
