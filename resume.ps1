<#
.SYNOPSIS
    One-click resume for the Job Tracker Android App project.
    Prints session summary and opens key files.
#>

Write-Host ""
Write-Host "╔══════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║     Job Tracker Android App — Resume Session    ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Project root
$root = Split-Path -Parent $MyInvocation.MyCommand.Path

# --- Check file integrity ---
$essential = @(
    @{Path="info.md"; Label="Master spec"},
    @{Path="link.txt"; Label="DeepSeek link"},
    @{Path="session_state.md"; Label="Session state"},
    @{Path="conversation.md"; Label="DeepSeek conversation"}
)

Write-Host "📁 File Check:" -ForegroundColor Yellow
$allGood = $true
foreach ($f in $essential) {
    $full = Join-Path $root $f.Path
    if (Test-Path $full) {
        $size = (Get-Item $full).Length
        Write-Host "  ✅ $($f.Path) ($($f.Label)) — $($size / 1KB)N0 KB" -ForegroundColor Green
    } else {
        Write-Host "  ❌ $($f.Path) — MISSING" -ForegroundColor Red
        $allGood = $false
    }
}

# --- Project stats ---
$infoLines = (Get-Content (Join-Path $root "info.md")).Count
Write-Host ""
Write-Host "📊 Project Stats:" -ForegroundColor Yellow
Write-Host "  Spec size: $infoLines lines"
Write-Host "  Sections:  $(Select-String (Join-Path $root 'info.md') -Pattern '^## ' | Measure-Object | Select-Object -ExpandProperty Count)"
Write-Host "  Features spec'd: 40+ across 8 tiers"

# --- Last topic ---
$sessionState = Join-Path $root "session_state.md"
if (Test-Path $sessionState) {
    $lastTopic = Select-String $sessionState -Pattern "^## Last Discussed" -Context 0,3
    if ($lastTopic) {
        Write-Host ""
        Write-Host "💬 Last Topic:" -ForegroundColor Yellow
        Write-Host "  $($lastTopic.Context.PostContext[1])"
    }
}

# --- Next step ---
Write-Host ""
Write-Host "🎯 Next Step (from build plan):" -ForegroundColor Yellow
Write-Host "  Phase 1 — Generate ParserEngine.kt (code not started)"
Write-Host ""

# --- Menu ---
Write-Host "What would you like to do?" -ForegroundColor Cyan
Write-Host "  1. Open info.md (full spec)"
Write-Host "  2. Open session_state.md (resume context)"
Write-Host "  3. Show project file tree"
Write-Host "  4. Start coding (generate Android project scaffold)"
Write-Host "  5. Exit"
Write-Host ""

$choice = Read-Host "Enter choice (1-5)"

switch ($choice) {
    "1" { Invoke-Item (Join-Path $root "info.md") }
    "2" { Invoke-Item (Join-Path $root "session_state.md") }
    "3" {
        Write-Host ""
        Write-Host "📂 Project Files:" -ForegroundColor Yellow
        Get-ChildItem -Path $root -Recurse -File | ForEach-Object {
            Write-Host "  $($_.Name) ($($_.Length / 1KB)N0 KB)"
        }
    }
    "4" {
        Write-Host ""
        Write-Host "🚀 Ready to start coding!" -ForegroundColor Green
        Write-Host "Tell the AI: 'Generate the Android project scaffold for the Job Tracker app'"
    }
    "5" { Write-Host "Goodbye!" }
    default { Write-Host "Invalid choice." }
}

Write-Host ""
Write-Host "Done." -ForegroundColor Cyan
