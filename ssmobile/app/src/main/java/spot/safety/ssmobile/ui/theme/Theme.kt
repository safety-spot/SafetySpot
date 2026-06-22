package spot.safety.ssmobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreen,
    secondary = BrandCyan,
    tertiary = PointsYellow,
    background = Color(0xFF101820),
    surface = Color(0xFF182430),
    onPrimary = Color.White,
    onSecondary = BrandBlue,
    onTertiary = BrandBlue,
    onBackground = Color(0xFFEAF2F8),
    onSurface = Color(0xFFEAF2F8)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    secondary = BrandBlue,
    tertiary = PointsYellow,
    background = AppBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = BrandBlue,
    onBackground = BrandBlue,
    onSurface = BrandBlue
)

@Composable
fun SsmobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
