package spot.safety.ssmobile.util

import java.text.NumberFormat
import java.util.Locale

fun formatScore(points: Int): String =
    NumberFormat.getNumberInstance(Locale.GERMANY).format(points)
