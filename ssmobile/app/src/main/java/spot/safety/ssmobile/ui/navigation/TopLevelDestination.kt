package spot.safety.ssmobile.ui.navigation

enum class TopLevelDestination(
    val route: String,
    val label: String,
    val iconText: String
) {
    HOME(Destinations.HOME, "Start", "H"),
    SCENARIOS(Destinations.SCENARIOS, "Szenarien", "S"),
    RANKING(Destinations.RANKING, "Ranking", "R"),
    PROFILE(Destinations.PROFILE, "Profil", "P")
}
