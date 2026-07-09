$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot'
$env:PATH = 'C:\Users\1\.m2\wrapper\dists\apache-maven-3.9.9-bin\841d5b83\apache-maven-3.9.9\bin;' + $env:PATH

Set-Location $PSScriptRoot

Write-Host "JAVA_HOME: $env:JAVA_HOME"
Write-Host "Starting Spring Boot..."
mvn spring-boot:run
