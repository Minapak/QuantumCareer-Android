package com.swiftquantum.quantumcareer.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// ========== QuantumCareer Brand Colors - Career-Gold (#FFB800) ==========
// Primary Brand Colors
val CareerGold = Color(0xFFFFB800)
val CareerGoldLight = Color(0xFFFFCC33)
val CareerGoldDark = Color(0xFFCC9200)
val CareerGoldContainer = Color(0xFF3D2E00)
val OnCareerGoldContainer = Color(0xFFFFECB3)

// Legacy aliases for compatibility
val QuantumPurple = CareerGold
val QuantumPurpleDark = CareerGoldDark
val QuantumTeal = Color(0xFF03DAC6)
val QuantumTealDark = Color(0xFF018786)

// Badge colors
val BadgeBronze = Color(0xFFCD7F32)
val BadgeSilver = Color(0xFFC0C0C0)
val BadgeGold = CareerGold
val BadgePlatinum = Color(0xFFE5E4E2)

// Status colors
val StatusPublished = Color(0xFF4CAF50)
val StatusUnderReview = Color(0xFFFF9800)
val StatusDraft = Color(0xFF9E9E9E)
val StatusRejected = Color(0xFFF44336)

// ========== Unified Ecosystem Background Colors ==========
val BackgroundDark = Color(0xFF0A0A0A)
val SurfaceDark = Color(0xFF121212)
val SurfaceDarkVariant = Color(0xFF1E1E1E)
val OnSurfaceDark = Color(0xFFFFFFFF)
val OnSurfaceDarkVariant = Color(0xFFB2BEC3)

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0AEC0)
val TextDisabled = Color(0xFF4A5568)

// Success and Error Colors
val SuccessGreen = Color(0xFF4CAF50)
val ErrorRed = Color(0xFFF44336)
val WarningOrange = Color(0xFFFF9800)

// ========== Ecosystem App Colors ==========
val NativeBlue = Color(0xFF0066FF)
val SwiftPurple = Color(0xFF7B2FFF)
val BridgeCyan = Color(0xFF00D4FF)

// ========== SQC Fidelity Grade Colors (Australian Standards v5.2.0) ==========
val FidelityPlatinum = Color(0xFFE5E4E2)  // Platinum - >= 99.9%
val FidelityGold = Color(0xFFFFD700)       // Gold - >= 99.5%
val FidelitySilver = Color(0xFFC0C0C0)     // Silver - >= 99.0%
val FidelityBronze = Color(0xFFCD7F32)     // Bronze - >= 98.0%
val FidelityStandard = Color(0xFF4A90D9)   // Standard - >= 95.0%
val FidelityDeveloping = Color(0xFF808080) // Developing - < 95.0%

// SQC Brand Colors
val SQCBlue = Color(0xFF0052CC)
val SQCLightBlue = Color(0xFF2684FF)
val SQCAustralianGold = Color(0xFFFFCD00)

// ========== QuantumColors Object for Admin/UI ==========
object QuantumColors {
    // Core colors
    val Primary = CareerGold
    val primaryVariant = CareerGoldDark
    val Secondary = QuantumTeal
    val secondaryVariant = QuantumTealDark
    val Accent = CareerGoldLight

    // Background colors
    val Background = BackgroundDark
    val Surface = SurfaceDark
    val surfaceVariant = SurfaceDarkVariant
    val CardBackground = SurfaceDarkVariant

    // Text colors
    val TextPrimary = com.swiftquantum.quantumcareer.presentation.ui.theme.TextPrimary
    val TextSecondary = com.swiftquantum.quantumcareer.presentation.ui.theme.TextSecondary
    val TextTertiary = com.swiftquantum.quantumcareer.presentation.ui.theme.TextDisabled
    val TextDisabled = com.swiftquantum.quantumcareer.presentation.ui.theme.TextDisabled

    // On colors
    val onPrimary = Color(0xFF000000)
    val onSecondary = Color(0xFF000000)
    val onBackground = com.swiftquantum.quantumcareer.presentation.ui.theme.TextPrimary
    val onSurface = OnSurfaceDark
    val onSurfaceVariant = OnSurfaceDarkVariant

    // Status colors
    val Error = ErrorRed
    val onError = Color(0xFFFFFFFF)
    val Success = SuccessGreen
    val Warning = WarningOrange

    // Border/Divider
    val Border = Color(0xFF2D2D2D)
    val Divider = Color(0xFF2D2D2D)

    // Badge colors
    val Gold = BadgeGold
    val badgeBronze = BadgeBronze
    val badgeSilver = BadgeSilver
    val badgeGold = BadgeGold
    val badgePlatinum = BadgePlatinum

    // Additional colors for onboarding
    val Info = Color(0xFF2196F3)
    val BackgroundSecondary = Color(0xFF161616)
}
