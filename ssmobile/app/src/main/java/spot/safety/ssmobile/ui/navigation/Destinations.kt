package spot.safety.ssmobile.ui.navigation

object Destinations {
    const val AUTH = "auth"
    const val HOME = "home"
    const val SCENARIOS = "scenarios"
    const val RANKING = "ranking"
    const val PROFILE = "profile"
    const val SCENARIO_ID_ARG = "scenarioId"
    const val SCENARIO_PLAY = "scenario_play/{$SCENARIO_ID_ARG}"

    fun scenarioPlayRoute(scenarioId: Int): String = "scenario_play/$scenarioId"
}
