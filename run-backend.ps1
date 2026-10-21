# run-backend.ps1 - Farm2Home Backend Launcher
$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

$envPath = Join-Path $scriptDir ".env"
if (Test-Path $envPath) {
    Write-Host "Loading environment variables from .env..."
    $lines = [System.IO.File]::ReadAllLines($envPath, [System.Text.Encoding]::UTF8)
    foreach ($rawLine in $lines) {
        $line = $rawLine.Trim().Trim("`u{FEFF}`r`n `t")
        if ($line -and -not $line.StartsWith("#")) {
            $parts = $line.Split("=", 2)
            if ($parts.Length -eq 2) {
                $name = $parts[0].Trim().Trim("`u{FEFF}`r`n `t")
                $val = $parts[1].Trim().Trim("`r`n `t")
                if (($val.StartsWith('"') -and $val.EndsWith('"')) -or ($val.StartsWith("'") -and $val.EndsWith("'"))) {
                    if ($val.Length -ge 2) {
                        $val = $val.Substring(1, $val.Length - 2)
                    }
                }
                $val = $val.Trim().Trim("`r`n `t")
                [System.Environment]::SetEnvironmentVariable($name, $val, "Process")
                Write-Host "Loaded env variable: $name"
            }
        }
    }
} else {
    Write-Warning ".env file not found at $envPath"
}

# Guardrail: Verify all required variables are present and non-empty
$requiredVars = @("RAZORPAY_KEY_ID", "RAZORPAY_KEY_SECRET", "DB_PASSWORD", "JWT_SECRET")
foreach ($var in $requiredVars) {
    $val = [System.Environment]::GetEnvironmentVariable($var, "Process")
    if ([string]::IsNullOrWhiteSpace($val)) {
        Write-Host "Missing variable: $var"
        exit 1
    }
}

$env:MAVEN_OPTS = "-Xmx256m"

if (Test-Path (Join-Path $scriptDir "mvnw.cmd")) {
    & (Join-Path $scriptDir "mvnw.cmd") spring-boot:run "-Dspring-boot.run.jvmArguments=-Xmx512m"
} else {
    mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-Xmx512m"
}
