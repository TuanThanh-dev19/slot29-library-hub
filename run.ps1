# Author: Phan Tuan Thanh
[CmdletBinding()]
param([ValidateSet('sqlserver', 'h2')][string]$Profile = 'sqlserver')
$ErrorActionPreference = 'Stop'
if ($Profile -eq 'sqlserver' -and -not (Test-Path -LiteralPath (Join-Path $PSScriptRoot 'slot 29/mssql-jdbc_auth-12.8.1.x64.dll'))) {
    & (Join-Path $PSScriptRoot 'setup-windows-auth.ps1')
}
Push-Location (Join-Path $PSScriptRoot 'slot 29')
try {
    & mvn -B -ntp package
    if ($LASTEXITCODE -ne 0) { throw 'Build or tests failed.' }
    Write-Host 'Open http://localhost:8080 after startup. Press Ctrl+C to stop.'
    & java -jar target/slot29-library-hub-1.0.0.jar "--spring.profiles.active=$Profile,demo"
    if ($LASTEXITCODE -ne 0) { throw 'Startup failed. Check SQL Server and run sql/setup-sqlserver.sql in SSMS.' }
} finally { Pop-Location }
