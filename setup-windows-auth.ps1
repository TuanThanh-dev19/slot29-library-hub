# Author: Phan Tuan Thanh
# Official Microsoft JDBC authentication library from Maven Central.
$ErrorActionPreference = 'Stop'
$dllName = 'mssql-jdbc_auth-12.8.1.x64.dll'
$uri = "https://repo.maven.apache.org/maven2/com/microsoft/sqlserver/mssql-jdbc_auth/12.8.1.x64/$dllName"
$destination = Join-Path $PSScriptRoot $dllName
Invoke-WebRequest -Uri $uri -OutFile $destination
Copy-Item -LiteralPath $destination -Destination (Join-Path (Join-Path $PSScriptRoot 'slot 29') $dllName) -Force
Write-Host 'Windows Authentication is ready for Java x64 when run from the project root or Maven project directory.'
