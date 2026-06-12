package com.jobtracker.ui.theme

import androidx.compose.ui.graphics.Color

// ── Background & Surface Layer ─────────────────────────────────────────────
// Three visibly distinct layers: bg → elevated surface → container
val DeepNavy = Color(0xFF0A0E27)        // Main background
val DarkSurface = Color(0xFF1A2150)     // Cards, sheets — distinct from bg
val SlateBlue = Color(0xFF252D6A)       // Containers, input bg — one step up

// ── Text (100% opaque — WCAG AA 4.5:1 minimum on all dark surfaces) ───────
val TextPrimary = Color(0xFFF0F2F8)     // Main body text — slightly warm white
val TextSecondary = Color(0xFFB2BBD4)   // Secondary labels — 7.4:1 on DarkSurface
val TextTertiary = Color(0xFF8892B5)    // Placeholders, hints — 4.75:1 on DarkSurface

// ── Glass / Frost Effects (visible opacity) ────────────────────────────────
val GlassWhite = Color(0xFFF0F2F8)      // Fully opaque white-text alias
val GlassWhiteLight = Color(0x99FFFFFF)  // 60% white — overlay tint
val GlassUltraLight = Color(0x3DFFFFFF) // 24% white — subtle tint
val GlassBorder = Color(0x4DFFFFFF)     // 30% white — clearly visible border
val GlassShadow = Color(0x60000000)     // Drop-shadow

// ── Accent Colors (lightened for WCAG AA on dark backgrounds) ─────────────
val SoftLavender = Color(0xFF8B9BEB)    // Primary accent — 7.7:1 on DeepNavy
val VibrantMint = Color(0xFF6EE7B7)     // Success / secondary
val CoralPink = Color(0xFFFB7185)       // Danger / delete
val WarmAmber = Color(0xFFFBBF24)       // Warning
val CoolGray = Color(0xFF9CA3AF)        // Muted / disabled (5.5:1 on DarkSurface)

// ── iOS System Colors ─────────────────────────────────────────────────────
val iOSBlue = Color(0xFF007AFF)
val iOSRed = Color(0xFFFF3B30)
val iOSGreen = Color(0xFF34C759)
val iOSOrange = Color(0xFFFF9500)
val iOSPurple = Color(0xFFAF52DE)
val iOSYellow = Color(0xFFFFCC00)

// ── Blob Gradient Colors ──────────────────────────────────────────────────
val BlobBlue1 = Color(0xFF4F46E5)
val BlobBlue2 = Color(0xFF7C3AED)
val BlobGreen1 = Color(0xFF059669)
val BlobGreen2 = Color(0xFF0D9488)
val BlobTeal1 = Color(0xFF0891B2)
val BlobTeal2 = Color(0xFF0E7490)
