package spot.safety.ssmobile.ui.navigation

object Destinations {
    const val AUTH = "auth"
    const val HOME = "home"
    const val SCENARIOS = "scenarios"
    const val RANKING = "ranking"
    const val PROFILE = "profile"
    const val PROFILE_PROGRESS = "profile_progress"
    const val PROFILE_BADGES = "profile_badges"
    const val PROFILE_SCENARIOS = "profile_scenarios"
    const val SETTINGS = "settings"
    const val HELP = "help"
    const val SCENARIO_ID_ARG = "scenarioId"
    const val SCENARIO_DETAIL = "scenario_detail/{$SCENARIO_ID_ARG}"
    const val SCENARIO_PLAY = "scenario_play/{$SCENARIO_ID_ARG}"

    fun scenarioDetailRoute(scenarioId: Int): String = "scenario_detail/$scenarioId"
    fun scenarioPlayRoute(scenarioId: Int): String = "scenario_play/$scenarioId"
}
