package dk.nenolink.dapl.domain.navigation

import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.domain.model.CourseCatalog

/**
 * Screen navigation for the app.
 *
 * This reducer is not the dialogue FSM. A future dialogue machine may change
 * the turn inside a scenario screen. It must not push or pop these destinations.
 */
object AppNavigator {
    fun reduce(state: NavigationState, event: NavEvent, catalog: CourseCatalog): NavigationState {
        return when (event) {
            NavEvent.Back ->
                if (state.stack.size > 1) state.copy(stack = state.stack.dropLast(1)) else state
            is NavEvent.OpenModule -> openModule(state, event.route)
            is NavEvent.OpenLesson -> openLesson(state, event.lessonId, catalog)
            is NavEvent.OpenScenario -> openScenario(state, event.scenarioId, catalog)
        }
    }

    private fun openModule(state: NavigationState, route: AppRoute): NavigationState {
        if (state.current != Destination.Home || route == AppRoute.HOME) return state
        if (route !in AppRoute.menuOrder) return state
        return state.push(route.toDestination())
    }

    private fun openLesson(
        state: NavigationState,
        lessonId: String,
        catalog: CourseCatalog
    ): NavigationState {
        val moduleRoute = when (state.current) {
            Destination.Level1 -> AppRoute.LEVEL1
            Destination.Level2 -> AppRoute.LEVEL2
            Destination.Level3 -> AppRoute.LEVEL3
            else -> return state
        }
        val module = catalog.module(moduleRoute) ?: return state
        val lesson = catalog.lesson(lessonId) ?: return state
        if (lesson.moduleId != module.id) return state
        return state.push(Destination.Lesson(lessonId))
    }

    private fun openScenario(
        state: NavigationState,
        scenarioId: String,
        catalog: CourseCatalog
    ): NavigationState {
        if (state.current != Destination.Conversation) return state
        if (catalog.scenario(scenarioId) == null) return state
        return state.push(Destination.Scenario(scenarioId))
    }
}

data class NavigationState(val stack: List<Destination>) {
    init {
        require(stack.isNotEmpty()) { "Navigation stack cannot be empty" }
        require(stack.first() == Destination.Home) { "Navigation starts at home" }
    }

    val current: Destination get() = stack.last()

    fun push(destination: Destination): NavigationState = copy(stack = stack + destination)

    companion object {
        fun home(): NavigationState = NavigationState(listOf(Destination.Home))
    }
}

sealed interface Destination {
    data object Home : Destination
    data object Level1 : Destination
    data object Level2 : Destination
    data object Level3 : Destination
    data object Conversation : Destination
    data object Quiz : Destination
    data object Grammar : Destination
    data object Children : Destination
    data object About : Destination
    data class Lesson(val lessonId: String) : Destination
    data class Scenario(val scenarioId: String) : Destination
}

sealed interface NavEvent {
    data class OpenModule(val route: AppRoute) : NavEvent
    data class OpenLesson(val lessonId: String) : NavEvent
    data class OpenScenario(val scenarioId: String) : NavEvent
    data object Back : NavEvent
}

fun AppRoute.toDestination(): Destination = when (this) {
    AppRoute.HOME -> Destination.Home
    AppRoute.LEVEL1 -> Destination.Level1
    AppRoute.LEVEL2 -> Destination.Level2
    AppRoute.LEVEL3 -> Destination.Level3
    AppRoute.CONVERSATION -> Destination.Conversation
    AppRoute.QUIZ -> Destination.Quiz
    AppRoute.GRAMMAR -> Destination.Grammar
    AppRoute.CHILDREN -> Destination.Children
    AppRoute.ABOUT -> Destination.About
}
