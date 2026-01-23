# regression-v1.ps1
# GooSage v1.0 Regression: login -> coach -> (optional) wrongs -> event -> coach -> today -> streak -> host mismatch check
# Usage:
#   powershell -ExecutionPolicy Bypass -File .\regression-v1.ps1
# Optional env:
#   $env:BASE="http://localhost:8084"
#   $env:EMAIL="ps@test.com"
#   $env:PASS="1234"

$ErrorActionPreference = "Stop"

# =========================
# 0) Config
# =========================
$base  = if ($env:BASE)  { $env:BASE }  else { "http://localhost:8084" }
$email = if ($env:EMAIL) { $env:EMAIL } else { "ps@test.com" }
$pass  = if ($env:PASS)  { $env:PASS }  else { "1234" }

function Print-Json($title, $obj) {
  Write-Host ""
  Write-Host "==== $title ====" -ForegroundColor Cyan
  try {
    ($obj | ConvertTo-Json -Depth 50)
  } catch {
    $obj
  }
}

function Read-ErrorBody($err) {
  try {
    $resp = $err.Exception.Response
    if ($null -eq $resp) { return $err.ToString() }
    $stream = $resp.GetResponseStream()
    if ($null -eq $stream) { return $err.ToString() }
    $r = New-Object IO.StreamReader($stream)
    return $r.ReadToEnd()
  } catch {
    return $err.ToString()
  }
}

function Call-Api($title, [scriptblock]$fn) {
  try {
    $res = & $fn
    Print-Json $title $res
    return $res
  } catch {
    Write-Host ""
    Write-Host "==== $title (ERROR) ====" -ForegroundColor Red
    $body = Read-ErrorBody $_
    Write-Host $body
    throw
  }
}

# 세션 쿠키 보관
$cj = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# =========================
# 1) Login
# =========================
$loginBody = @{ email = $email; password = $pass } | ConvertTo-Json
Call-Api "LOGIN" {
  irm -Method Post "$base/auth/login" -ContentType "application/json" -Body $loginBody -WebSession $cj
} | Out-Null

# =========================
# 2) Coach (state + nextAction)
# =========================
$coach = Call-Api "COACH (BEFORE)" {
  irm "$base/study/coach" -WebSession $cj
}

# 안전하게 nextAction 파싱
$next = $coach.data.nextAction
$nextType = $next.type
$kid = $next.knowledgeId

Write-Host ""
Write-Host "NextAction: $nextType / knowledgeId=$kid / requiresForge=$($next.requiresForge)" -ForegroundColor Yellow

# =========================
# 3) If nextAction has knowledgeId -> call wrong endpoint
# =========================
if ($null -ne $kid -and $kid -gt 0) {
  $wrong = Call-Api "GET WRONG (knowledge/$kid)" {
    irm "$base/knowledge/$kid/quiz/wrong" -WebSession $cj
  }

  $wrongCount = 0
  try { $wrongCount = $wrong.data.wrong.Count } catch { $wrongCount = 0 }

  Write-Host ""
  Write-Host "WrongCount: $wrongCount" -ForegroundColor Yellow
} else {
  Write-Host ""
  Write-Host "No knowledgeId in nextAction -> skip /knowledge/{id}/quiz/wrong call" -ForegroundColor DarkYellow
}

# =========================
# 4) Record one event (simulate completing the suggested action)
# =========================
# 이벤트 타입은 네가 이미 쓰는 것 그대로 고정
# - 오답 행동이든 아니든, 최소 행동 1회 기록이 핵심
$eventBody = @{ type = "WRONG_REVIEW_DONE"; knowledgeId = $kid } | ConvertTo-Json
Call-Api "POST STUDY EVENT (WRONG_REVIEW_DONE)" {
  irm -Method Post "$base/study/events" -ContentType "application/json" -Body $eventBody -WebSession $cj
} | Out-Null

# =========================
# 5) Coach again (verify immediate reflection)
# =========================
Call-Api "COACH (AFTER)" {
  irm "$base/study/coach" -WebSession $cj
} | Out-Null

# =========================
# 6) Today / Streak
# =========================
Call-Api "TODAY" {
  irm "$base/study/today" -WebSession $cj
} | Out-Null

Call-Api "STREAK" {
  irm "$base/study/streak" -WebSession $cj
} | Out-Null

# =========================
# 7) Host mismatch check (localhost session should NOT work on 127.0.0.1)
# =========================
$base2 = $base -replace "localhost", "127.0.0.1"
if ($base2 -ne $base) {
  try {
    irm "$base2/study/coach" -WebSession $cj | Out-Null
    Write-Host ""
    Write-Host "HOST MISMATCH: Unexpectedly succeeded on $base2 (check cookie domain handling)" -ForegroundColor Red
  } catch {
    Write-Host ""
    Write-Host "HOST MISMATCH: Expected 401 on $base2 " -ForegroundColor Green
  }
} else {
  Write-Host ""
  Write-Host "HOST MISMATCH: base is not localhost, skipped" -ForegroundColor DarkYellow
}

Write-Host ""
Write-Host "Regression completed." -ForegroundColor Green

