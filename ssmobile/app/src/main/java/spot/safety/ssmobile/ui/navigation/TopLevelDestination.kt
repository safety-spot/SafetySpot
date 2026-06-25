package spot.safety.ssmobile.ui.navigation

import androidx.annotation.DrawableRes
import spot.safety.ssmobile.R

enum class TopLevelDestination(
    val route: String,
    val label: String,
    @param:DrawableRes val iconRes: Int
) {
    HOME(Destinations.HOME, "Start", R.drawable.ic_home),
    SCENARIOS(Destinations.SCENARIOS, "Szenarien", R.drawable.ic_scenarios),
    RANKING(Destinations.RANKING, "Ranking", R.drawable.ic_ranking),
    PROFILE(Destinations.PROFILE, "Profil", R.drawable.ic_profile)
}
