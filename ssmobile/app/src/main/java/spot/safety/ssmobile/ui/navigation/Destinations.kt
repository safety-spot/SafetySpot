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
    const val SCENARIO_CATEGORY_ARG = "scenarioCategory"
    const val SCENARIO_DETAIL = "scenario_detail/{$SCENARIO_CATEGORY_ARG}"
    const val SCENARIO_PLAY = "scenario_play/{$SCENARIO_CATEGORY_ARG}"

    fun scenarioDetailRoute(category: String): String = "scenario_detail/${category.encodeUrl()}"
    fun scenarioPlayRoute(category: String): String = "scenario_play/${category.encodeUrl()}"

    private fun String.encodeUrl() = java.net.URLEncoder.encode(this, "UTF-8")
}
