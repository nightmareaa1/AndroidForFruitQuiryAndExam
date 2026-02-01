# PowerShell script to make shell scripts executable on Windows
# This is mainly for documentation - Git will handle executable permissions

Write-Host "📝 Setting executable permissions for shell scripts..."

$scriptFiles = @(
    "test-local.sh",
    "setup-emulator.sh"
)

foreach ($file in $scriptFiles) {
    if (Test-Path $file) {
        Write-Host "✅ $file - permissions noted for Git"
    } else {
        Write-Host "❌ $file - not found"
    }
}

Write-Host ""
Write-Host "ℹ️  Note: On Windows, use .bat files for local execution"
Write-Host "   On Linux/macOS, use .sh files with: chmod +x *.sh"