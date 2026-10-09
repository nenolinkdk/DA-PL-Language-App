package dk.nenolink.dapl.domain.navigation

import dk.nenolink.dapl.domain.content.CourseLibraryLoader
import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.domain.model.BilingualText
import dk.nenolink.dapl.domain.model.ScenarioIndexEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppNavigatorTest {
    private val catalog = CourseLibraryLoader.loadBundled().catalog

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
    fun unknownScenarioDoesNotOpen() {
        val conversation = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenModule(AppRoute.CONVERSATION),
            catalog
        )
        val next = AppNavigator.reduce(
            conversation,
            NavEvent.OpenScenario("dlg-missing"),
            catalog
        )
        assertEquals(Destination.Conversation, next.current)
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

    @Test
    fun quizOpensOnlyFromTheReleasedLessonAndBackReturnsThere() {
        assertTrue(catalog.lesson("lesson-01")!!.hasQuiz)
        val level = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenModule(AppRoute.LEVEL1),
            catalog
        )
        val rejectedFromLevel = AppNavigator.reduce(level, NavEvent.OpenQuiz("lesson-01"), catalog)
        assertEquals(Destination.Level1, rejectedFromLevel.current)

        val lesson = AppNavigator.reduce(level, NavEvent.OpenLesson("lesson-01"), catalog)
        val quiz = AppNavigator.reduce(lesson, NavEvent.OpenQuiz("lesson-01"), catalog)
        assertEquals(Destination.LessonQuiz("lesson-01"), quiz.current)
        val back = AppNavigator.reduce(quiz, NavEvent.Back, catalog)
        assertEquals(Destination.Lesson("lesson-01"), back.current)
    }

    @Test
    fun unreleasedLessonDoesNotOpenAQuiz() {
        val level = AppNavigator.reduce(
            NavigationState.home(),
            NavEvent.OpenModule(AppRoute.LEVEL1),
            catalog
        )
        val lesson = AppNavigator.reduce(level, NavEvent.OpenLesson("lesson-02"), catalog)
        val quiz = AppNavigator.reduce(lesson, NavEvent.OpenQuiz("lesson-02"), catalog)
        assertEquals(Destination.Lesson("lesson-02"), quiz.current)
    }
}
