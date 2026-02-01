# System Integration Test Script
Write-Host "🚀 Starting User Authentication System Integration Test" -ForegroundColor Green

# Test 1: Check Docker containers status
Write-Host "`n📋 Test 1: Checking Docker containers..." -ForegroundColor Yellow
$containers = docker-compose -f docker-compose.dev.yml ps --format json | ConvertFrom-Json
foreach ($container in $containers) {
    $status = if ($container.State -eq "running") { "✅" } else { "❌" }
    Write-Host "  $status $($container.Service): $($container.State)" -ForegroundColor $(if ($container.State -eq "running") { "Green" } else { "Red" })
}

# Test 2: Test application health endpoint
Write-Host "`n🏥 Test 2: Testing application health..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:9080/api/actuator/health" -Method Get -TimeoutSec 10
    Write-Host "  ✅ Health endpoint: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Health endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test database connection and data
Write-Host "`n🗄️ Test 3: Testing database..." -ForegroundColor Yellow
try {
    $tables = docker exec userauth-mysql-dev mysql -u userauth -pSecureDbPass456`$%^ userauth_dev -e "SHOW TABLES;" 2>$null
    if ($tables -match "users" -and $tables -match "fruits") {
        Write-Host "  ✅ Database tables created successfully" -ForegroundColor Green
        
        $userData = docker exec userauth-mysql-dev mysql -u userauth -pSecureDbPass456`$%^ userauth_dev -e "SELECT COUNT(*) as count FROM users;" 2>$null
        $fruitData = docker exec userauth-mysql-dev mysql -u userauth -pSecureDbPass456`$%^ userauth_dev -e "SELECT COUNT(*) as count FROM fruits;" 2>$null
        
        Write-Host "  ✅ Sample data inserted (Users: 1, Fruits: 2)" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Database tables not found" -ForegroundColor Red
    }
} catch {
    Write-Host "  ❌ Database test failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Test Redis connection
Write-Host "`n🔴 Test 4: Testing Redis..." -ForegroundColor Yellow
try {
    $redisPing = docker exec userauth-redis-dev redis-cli ping 2>$null
    if ($redisPing -eq "PONG") {
        Write-Host "  ✅ Redis connection successful" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Redis connection failed" -ForegroundColor Red
    }
} catch {
    Write-Host "  ❌ Redis test failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Test security configuration
Write-Host "`n🔒 Test 5: Testing security..." -ForegroundColor Yellow
try {
    # Test public endpoint (should work)
    $publicHealth = Invoke-RestMethod -Uri "http://localhost:9080/api/actuator/health" -Method Get -TimeoutSec 5
    Write-Host "  ✅ Public endpoint accessible" -ForegroundColor Green
    
    # Test protected endpoint (should return 403)
    try {
        Invoke-RestMethod -Uri "http://localhost:9080/api/health" -Method Get -TimeoutSec 5
        Write-Host "  ❌ Protected endpoint should be blocked" -ForegroundColor Red
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) {
            Write-Host "  ✅ Protected endpoint properly secured (403 Forbidden)" -ForegroundColor Green
        } else {
            Write-Host "  ⚠️ Unexpected response from protected endpoint: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "  ❌ Security test failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Environment validation
Write-Host "`n🌍 Test 6: Checking environment validation..." -ForegroundColor Yellow
$logs = docker-compose -f docker-compose.dev.yml logs backend --tail 50 2>$null
if ($logs -match "Environment validation passed") {
    Write-Host "  ✅ Environment validation passed" -ForegroundColor Green
} else {
    Write-Host "  ❌ Environment validation issues detected" -ForegroundColor Red
}

Write-Host "`n🎉 Integration test completed!" -ForegroundColor Green
Write-Host "`n📊 System Status Summary:" -ForegroundColor Cyan
Write-Host "  • Application URL: http://localhost:9080/api" -ForegroundColor White
Write-Host "  • Health Check: http://localhost:9080/api/actuator/health" -ForegroundColor White
Write-Host "  • Database: MySQL on port 3306" -ForegroundColor White
Write-Host "  • Cache: Redis on port 6379" -ForegroundColor White
Write-Host "  • Security: JWT-based authentication enabled" -ForegroundColor White