$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot'
$env:PATH = 'C:\Users\1\.m2\wrapper\dists\apache-maven-3.9.9-bin\841d5b83\apache-maven-3.9.9\bin;' + $env:PATH

Set-Location $PSScriptRoot

Write-Host "JAVA_HOME: $env:JAVA_HOME"
Write-Host "Working Directory: $(Get-Location)"
Write-Host "Maven version:"
mvn --version

Write-Host "`n=== Compiling ==="
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8"
mvn clean compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n=== BUILD SUCCESS ==="
} else {
    Write-Host "`n=== BUILD FAILED ==="
    exit 1
}
