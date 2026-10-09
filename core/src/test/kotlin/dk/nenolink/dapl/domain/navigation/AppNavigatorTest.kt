package dk.nenolink.dapl.domain.navigation

import dk.nenolink.dapl.domain.content.CatalogLoader
import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.domain.model.BilingualText
import dk.nenolink.dapl.domain.model.ScenarioIndexEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppNavigatorTest {
    private val catalog = CatalogLoader.loadBundled()

    @Test
    fun everyMenuRouteOpensFromHomeAndBackReturnsHome() {
        AppRoute.menuOrder.forEach { route ->
            val opened = AppNavigator.reduce(
                NavigationState.home(),
                NavEvent.OpenModule(route),
                catalog
            )
            assertEquals(route.toDestination(), opened.current)
            val back = AppNavigator.reduce(opened, NavEvent.Back, catalog)
            assertEquals(Destination.Home, back.current)
        }
    }

    @Test
    fun backFromHomeStaysHome() {
        val next = AppNavigator.reduce(NavigationState.home(), NavEvent.Back, catalog)
        assertEquals(Destination.Home, next.current)
    }

    @Test
    fun modulesCannotBeOpenedFromAnotherModule() {
        val level1 = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenModule(AppRoute.LEVEL1),
            catalog
        )
        val jumped = AppNavigator.reduce(level1, NavEvent.OpenModule(AppRoute.QUIZ), catalog)
        assertEquals(Destination.Level1, jumped.current)
    }

    @Test
    fun lessonCannotOpenFromHome() {
        val lessonId = catalog.module(AppRoute.LEVEL1)!!.lessons.first().id
        val next = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenLesson(lessonId),
            catalog
        )
        assertEquals(Destination.Home, next.current)
    }

    @Test
    fun knownLessonOpensFromItsLevelAndBackReturnsToThatLevel() {
        val lessonId = catalog.module(AppRoute.LEVEL1)!!.lessons.first().id
        val level = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenModule(AppRoute.LEVEL1),
            catalog
        )
        val lesson = AppNavigator.reduce(level, NavEvent.OpenLesson(lessonId), catalog)
        assertEquals(Destination.Lesson(lessonId), lesson.current)
        val back = AppNavigator.reduce(lesson, NavEvent.Back, catalog)
        assertEquals(Destination.Level1, back.current)
    }

    @Test
    fun unknownLessonDoesNotChangeTheLevel() {
        val level = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenModule(AppRoute.LEVEL1),
            catalog
        )
        val next = AppNavigator.reduce(level, NavEvent.OpenLesson("lesson-missing"), catalog)
        assertEquals(Destination.Level1, next.current)
    }

    @Test
    fun lessonFromAnotherLevelIsRejected() {
        val lessonId = catalog.module(AppRoute.LEVEL1)!!.lessons.first().id
        val level2 = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenModule(AppRoute.LEVEL2),
            catalog
        )
        val next = AppNavigator.reduce(level2, NavEvent.OpenLesson(lessonId), catalog)
        assertEquals(Destination.Level2, next.current)
    }

    @Test
    fun emptyScenarioListRejectsScenarioNavigation() {
        val conversation = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenModule(AppRoute.CONVERSATION),
            catalog
        )
        val next = AppNavigator.reduce(
            conversation,
            NavEvent.OpenScenario("dlg-cafe-001"),
            catalog
        )
        assertEquals(Destination.Conversation, next.current)
        assertTrue(catalog.scenarios.isEmpty())
    }

    @Test
    fun knownScenarioOpensOnlyFromConversationAndBackDiscardsIt() {
        val withScenario = catalog.copy(
            scenarios = listOf(
                ScenarioIndexEntry(
                    id = "dlg-test-001",
                    title = BilingualText(support = "Test", target = "")
                )
            )
        )
        val fromHome = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenScenario("dlg-test-001"),
            withScenario
        )
        assertEquals(Destination.Home, fromHome.current)

        val conversation = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenModule(AppRoute.CONVERSATION),
            withScenario
        )
        val scenario = AppNavigator.reduce(
            conversation,
            NavEvent.OpenScenario("dlg-test-001"),
            withScenario
        )
        assertEquals(Destination.Scenario("dlg-test-001"), scenario.current)
        val back = AppNavigator.reduce(scenario, NavEvent.Back, withScenario)
        assertEquals(Destination.Conversation, back.current)
        assertEquals(listOf(Destination.Home, Destination.Conversation), back.stack)
    }
}
