$r = Invoke-RestMethod 'http://localhost:8080/api/categories'
Write-Host "=== 一级分类 ==="
foreach ($cat in $r.data) {
    Write-Host "  $($cat.id) $($cat.name)"
    foreach ($sub in $cat.children) {
        Write-Host "    $($sub.id) $($sub.name)"
    }
}

Write-Host "`n=== 统计 ==="
$s = Invoke-RestMethod 'http://localhost:8080/api/statistics/summary?startDate=2026-07-01&endDate=2026-07-09'
Write-Host "  Total: $($s.data.totalAmount), Count: $($s.data.recordCount)"
