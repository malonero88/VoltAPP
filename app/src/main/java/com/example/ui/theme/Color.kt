package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ==========================================
// STATIC CONSTANTS (Raw colors)
// ==========================================
val LightPolishBg = Color(0xFFF7F9FF)
val LightPolishCardBg = Color(0xFFFFFFFF)
val LightPolishCardBorder = Color(0xFFE2E8F0)
val LightPolishCardBorderSubtle = Color(0xFFF1F5F9)

val LightPolishPrimary = Color(0xFF005CBB)
val LightPolishPrimaryDark = Color(0xFF00448E)
val LightPolishPrimaryLight = Color(0xFFEFF6FF)
val LightPolishPrimaryAccent = Color(0xFF2563EB)

val LightPolishTextPrimary = Color(0xFF0F172A)
val LightPolishTextSecondary = Color(0xFF475569)
val LightPolishTextTertiary = Color(0xFF94A3B8)

val LightPolishInputBg = Color(0xFFF8FAFC)
val LightPolishInputBorder = Color(0xFFCBD5E1)

val LightPolishStatusSuccess = Color(0xFF10B981)
val LightPolishStatusSuccessBg = Color(0xFFECFDF5)
val LightPolishStatusWarning = Color(0xFFF59E0B)
val LightPolishStatusWarningBg = Color(0xFFFEF3C7)
val LightPolishStatusError = Color(0xFFEF4444)
val LightPolishStatusErrorBg = Color(0xFFFEF2F2)

val LightPolishDarkContainer = Color(0xFF0F172A)
val LightPolishDarkContainerSurface = Color(0xFF1E293B)
val LightPolishDarkContainerBorder = Color(0xFF334155)

// Dark raw colors (Sleek Dark Slate Palette)
val DarkPolishBg = Color(0xFF0B0F17)
val DarkPolishCardBg = Color(0xFF161E2E)
val DarkPolishCardBorder = Color(0xFF243044)
val DarkPolishCardBorderSubtle = Color(0xFF1A2233)

val DarkPolishPrimary = Color(0xFF38BDF8)
val DarkPolishPrimaryDark = Color(0xFF0284C7)
val DarkPolishPrimaryLight = Color(0xFF1E293B)
val DarkPolishPrimaryAccent = Color(0xFF60A5FA)

val DarkPolishTextPrimary = Color(0xFFF8FAFC)
val DarkPolishTextSecondary = Color(0xFF94A3B8)
val DarkPolishTextTertiary = Color(0xFF64748B)

val DarkPolishInputBg = Color(0xFF0F172A)
val DarkPolishInputBorder = Color(0xFF334155)

val DarkPolishStatusSuccess = Color(0xFF34D399)
val DarkPolishStatusSuccessBg = Color(0xFF064E3B)
val DarkPolishStatusWarning = Color(0xFFFBBF24)
val DarkPolishStatusWarningBg = Color(0xFF78350F)
val DarkPolishStatusError = Color(0xFFF87171)
val DarkPolishStatusErrorBg = Color(0xFF7F1D1D)

val DarkPolishDarkContainer = Color(0xFF070A0F)
val DarkPolishDarkContainerSurface = Color(0xFF111827)
val DarkPolishDarkContainerBorder = Color(0xFF1F2937)

@Immutable
data class VoltCalcColors(
    val bg: Color,
    val cardBg: Color,
    val cardBorder: Color,
    val cardBorderSubtle: Color,
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val primaryAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val inputBg: Color,
    val inputBorder: Color,
    val statusSuccess: Color,
    val statusSuccessBg: Color,
    val statusWarning: Color,
    val statusWarningBg: Color,
    val statusError: Color,
    val statusErrorBg: Color,
    val darkContainer: Color,
    val darkContainerSurface: Color,
    val darkContainerBorder: Color,
    val isDark: Boolean
)

val LightVoltCalcColors = VoltCalcColors(
    bg = LightPolishBg,
    cardBg = LightPolishCardBg,
    cardBorder = LightPolishCardBorder,
    cardBorderSubtle = LightPolishCardBorderSubtle,
    primary = LightPolishPrimary,
    primaryDark = LightPolishPrimaryDark,
    primaryLight = LightPolishPrimaryLight,
    primaryAccent = LightPolishPrimaryAccent,
    textPrimary = LightPolishTextPrimary,
    textSecondary = LightPolishTextSecondary,
    textTertiary = LightPolishTextTertiary,
    inputBg = LightPolishInputBg,
    inputBorder = LightPolishInputBorder,
    statusSuccess = LightPolishStatusSuccess,
    statusSuccessBg = LightPolishStatusSuccessBg,
    statusWarning = LightPolishStatusWarning,
    statusWarningBg = LightPolishStatusWarningBg,
    statusError = LightPolishStatusError,
    statusErrorBg = LightPolishStatusErrorBg,
    darkContainer = LightPolishDarkContainer,
    darkContainerSurface = LightPolishDarkContainerSurface,
    darkContainerBorder = LightPolishDarkContainerBorder,
    isDark = false
)

val DarkVoltCalcColors = VoltCalcColors(
    bg = DarkPolishBg,
    cardBg = DarkPolishCardBg,
    cardBorder = DarkPolishCardBorder,
    cardBorderSubtle = DarkPolishCardBorderSubtle,
    primary = DarkPolishPrimary,
    primaryDark = DarkPolishPrimaryDark,
    primaryLight = DarkPolishPrimaryLight,
    primaryAccent = DarkPolishPrimaryAccent,
    textPrimary = DarkPolishTextPrimary,
    textSecondary = DarkPolishTextSecondary,
    textTertiary = DarkPolishTextTertiary,
    inputBg = DarkPolishInputBg,
    inputBorder = DarkPolishInputBorder,
    statusSuccess = DarkPolishStatusSuccess,
    statusSuccessBg = DarkPolishStatusSuccessBg,
    statusWarning = DarkPolishStatusWarning,
    statusWarningBg = DarkPolishStatusWarningBg,
    statusError = DarkPolishStatusError,
    statusErrorBg = DarkPolishStatusErrorBg,
    darkContainer = DarkPolishDarkContainer,
    darkContainerSurface = DarkPolishDarkContainerSurface,
    darkContainerBorder = DarkPolishDarkContainerBorder,
    isDark = true
)

val LocalVoltCalcColors = staticCompositionLocalOf { LightVoltCalcColors }

object AppTheme {
    val colors: VoltCalcColors
        @Composable
        @ReadOnlyComposable
        get() = LocalVoltCalcColors.current
}

// Dynamic Theme-aware Composable Color Accessors
val PolishBg: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.bg

val PolishCardBg: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.cardBg

val PolishCardBorder: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.cardBorder

val PolishCardBorderSubtle: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.cardBorderSubtle

val PolishPrimary: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.primary

val PolishPrimaryDark: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.primaryDark

val PolishPrimaryLight: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.primaryLight

val PolishPrimaryAccent: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.primaryAccent

val PolishTextPrimary: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.textPrimary

val PolishTextSecondary: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.textSecondary

val PolishTextTertiary: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.textTertiary

val PolishInputBg: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.inputBg

val PolishInputBorder: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.inputBorder

val PolishStatusSuccess: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusSuccess

val PolishStatusSuccessBg: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusSuccessBg

val PolishStatusWarning: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusWarning

val PolishStatusWarningBg: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusWarningBg

val PolishStatusError: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusError

val PolishStatusErrorBg: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusErrorBg

val PolishDarkContainer: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.darkContainer

val PolishDarkContainerSurface: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.darkContainerSurface

val PolishDarkContainerBorder: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.darkContainerBorder

// Legacy Aliases
val IndustrialBlack: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.bg

val IndustrialDark: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.cardBg

val IndustrialSurface: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.inputBg

val IndustrialSurfaceHigh: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.cardBorderSubtle

val IndustrialSurfaceHighest: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.cardBorder

val IndustrialBorder: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.cardBorder

val IndustrialBorderActive: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.primary

val ElectricYellow: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.primary

val ElectricYellowLight: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.primaryLight

val ElectricYellowDark: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.primaryDark

val SafetyOrange: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.primary

val SafetyOrangeLight: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.primaryLight

val TextPrimary: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.textPrimary

val TextSecondary: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.textSecondary

val TextTertiary: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.textTertiary

val StatusSuccess: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusSuccess

val StatusSuccessBg: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusSuccessBg

val StatusWarning: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusWarning

val StatusWarningBg: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusWarningBg

val StatusError: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusError

val StatusErrorBg: Color
    @Composable @ReadOnlyComposable get() = LocalVoltCalcColors.current.statusErrorBg
