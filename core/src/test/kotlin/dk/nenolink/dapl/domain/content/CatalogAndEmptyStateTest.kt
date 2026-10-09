package dk.nenolink.dapl.domain.content

import dk.nenolink.dapl.domain.model.AppIdentity
import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.domain.model.ModuleAvailability
import dk.nenolink.dapl.domain.navigation.Destination
import dk.nenolink.dapl.domain.navigation.toDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogAndEmptyStateTest {
    private val library = CourseLibraryLoader.loadBundled()
    private val catalog = library.catalog

    @Test
    fun applicationIdAndLocalesStayOnTheFrozenContract() {
        assertEquals("dk.nenolink.dapl", AppIdentity.APPLICATION_ID)
        assertEquals("da-DK", AppIdentity.SUPPORT_LOCALE)
        assertEquals("pl-PL", AppIdentity.TARGET_LOCALE)
    }

    @Test
    fun menuListsTheEightStructuralModulesInOrder() {
        val entries = library.menuEntries()
        assertEquals(AppRoute.menuOrder, entries.map { it.route })
        assertTrue(entries.all { it.label.isNotBlank() && it.detail.isNotBlank() })
    }

    @Test
    fun level1IndexesTheImportedLessonsAndOnlyLessonOneIsReleased() {
        val level1 = catalog.module(AppRoute.LEVEL1)!!
        assertEquals(ModuleAvailability.STRUCTURE_READY, level1.availability)
        assertEquals(10, level1.lessons.size)
        assertEquals("lesson-01", level1.lessons.first().id)
        assertEquals("Basale hilsner", level1.lessons.first().title.support)
        assertTrue(level1.lessons.first().released)
        assertTrue(level1.lessons.drop(1).all { !it.released && it.title.target.isBlank() })

        val screen = library.screenFor(Destination.Level1)
        assertEquals(ScreenKind.LESSON_LIST, screen.kind)
        assertEquals(level1.lessons.map { it.id }, screen.lessons.map { it.id })
        assertEquals("Klar", screen.lessons.first().statusLabel)
        assertTrue(screen.lessons.drop(1).all { !it.released && it.statusLabel == "Ikke udgivet endnu" })
        assertTrue(screen.body.isNotBlank())
    }

    @Test
    fun level2AndLevel3AreNotYetFilledAndDoNotCrash() {
        listOf(AppRoute.LEVEL2, AppRoute.LEVEL3).forEach { route ->
            val module = catalog.module(route)!!
            assertEquals(ModuleAvailability.NOT_YET_FILLED, module.availability)
            assertTrue(module.lessons.isEmpty())
            val screen = library.screenFor(route.toDestination())
            assertEquals(ScreenKind.UNAVAILABLE, screen.kind)
            assertTrue(screen.title.isNotBlank())
            assertTrue(screen.body.isNotBlank())
            assertTrue(screen.lessons.isEmpty())
            assertTrue(screen.scenarios.isEmpty())
        }
    }

    @Test
    fun unreleasedLessonScreenIsASafePlaceholder() {
        val lessonId = "lesson-02"
        val screen = library.screenFor(Destination.Lesson(lessonId))
        assertEquals(ScreenKind.UNAVAILABLE, screen.kind)
        assertEquals("Præsentationer", screen.title)
        assertTrue(screen.body.contains("ikke udgivet"))
        assertTrue(screen.lessons.isEmpty())
    }

    @Test
    fun missingLessonScreenIsStillSafe() {
        val screen = library.screenFor(Destination.Lesson("lesson-missing"))
        assertEquals(ScreenKind.UNAVAILABLE, screen.kind)
        assertTrue(screen.body.isNotBlank())
    }

    @Test
    fun conversationQuizGrammarChildrenAndAboutHaveExplicitEmptyOrInfoCopy() {
        val conversation = library.screenFor(Destination.Conversation)
        assertEquals(ScreenKind.CONVERSATION, conversation.kind)
        assertEquals(listOf("dlg-cafe-001", "dlg-ticket-001"), conversation.scenarios.map { it.id })
        assertTrue(conversation.body.contains("gemmes ikke"))

        val quiz = library.screenFor(Destination.Quiz)
        assertEquals(ScreenKind.UNAVAILABLE, quiz.kind)
        val children = library.screenFor(Destination.Children)
        assertEquals(ScreenKind.UNAVAILABLE, children.kind)
        val grammar = library.screenFor(Destination.Grammar)
        assertEquals(ScreenKind.GRAMMAR, grammar.kind)

        val about = library.screenFor(Destination.About)
        assertEquals(ScreenKind.ABOUT, about.kind)
        assertTrue(about.body.contains("da-DK"))
        assertTrue(about.body.contains("pl-PL"))
        assertTrue(about.body.contains("ikke af sig selv"))
    }

    @Test
    fun childrenModuleIsStructuralAndEmpty() {
        val children = catalog.module(AppRoute.CHILDREN)!!
        assertEquals(ModuleAvailability.NOT_YET_FILLED, children.availability)
        assertTrue(children.lessons.isEmpty())
    }

    @Test
    fun duplicateLessonIdIsRejected() {
        val broken = """
            {
              "schemaVersion": 1,
              "modules": [
                {
                  "id": "module-level1",
                  "route": "level1",
                  "order": 1,
                  "availability": "STRUCTURE_READY",
                  "lessons": [
                    {
                      "id": "lesson-same",
                      "moduleId": "module-level1",
                      "order": 1,
                      "title": { "support": "En", "target": "" },
                      "released": false
                    },
                    {
                      "id": "lesson-same",
                      "moduleId": "module-level1",
                      "order": 2,
                      "title": { "support": "To", "target": "" },
                      "released": false
                    }
                  ]
                },
                { "id": "module-level2", "route": "level2", "order": 2, "availability": "NOT_YET_FILLED", "lessons": [] },
                { "id": "module-level3", "route": "level3", "order": 3, "availability": "NOT_YET_FILLED", "lessons": [] },
                { "id": "module-conversation", "route": "conversation", "order": 4, "availability": "STRUCTURE_READY", "lessons": [] },
                { "id": "module-quiz", "route": "quiz", "order": 5, "availability": "STRUCTURE_READY", "lessons": [] },
                { "id": "module-grammar", "route": "grammar", "order": 6, "availability": "STRUCTURE_READY", "lessons": [] },
                { "id": "module-children", "route": "children", "order": 7, "availability": "NOT_YET_FILLED", "lessons": [] },
                { "id": "module-about", "route": "about", "order": 8, "availability": "STRUCTURE_READY", "lessons": [] }
              ],
              "scenarios": []
            }
        """.trimIndent()

        assertThrows(IllegalArgumentException::class.java) {
            CatalogLoader.load(broken)
        }
    }

    @Test
    fun notYetFilledModuleCannotSmuggleLessons() {
        val broken = """
            {
              "schemaVersion": 1,
              "modules": [
                { "id": "module-level1", "route": "level1", "order": 1, "availability": "NOT_YET_FILLED", "lessons": [
                  { "id": "lesson-l1-01-greetings", "moduleId": "module-level1", "order": 1, "title": { "support": "Hej", "target": "" }, "released": false }
                ] },
                { "id": "module-level2", "route": "level2", "order": 2, "availability": "NOT_YET_FILLED", "lessons": [] },
                { "id": "module-level3", "route": "level3", "order": 3, "availability": "NOT_YET_FILLED", "lessons": [] },
                { "id": "module-conversation", "route": "conversation", "order": 4, "availability": "STRUCTURE_READY", "lessons": [] },
                { "id": "module-quiz", "route": "quiz", "order": 5, "availability": "STRUCTURE_READY", "lessons": [] },
                { "id": "module-grammar", "route": "grammar", "order": 6, "availability": "STRUCTURE_READY", "lessons": [] },
                { "id": "module-children", "route": "children", "order": 7, "availability": "NOT_YET_FILLED", "lessons": [] },
                { "id": "module-about", "route": "about", "order": 8, "availability": "STRUCTURE_READY", "lessons": [] }
              ],
              "scenarios": []
            }
        """.trimIndent()

        assertThrows(IllegalArgumentException::class.java) {
            CatalogLoader.load(broken)
        }
    }

    @Test
    fun bundledCatalogReleasesOnlyLessonOneAndKeepsTwoScenarios() {
        assertEquals(listOf("dlg-cafe-001", "dlg-ticket-001"), catalog.scenarios.map { it.id })
        val released = catalog.modules.flatMap { it.lessons }.filter { it.released }.map { it.id }
        assertEquals(listOf("lesson-01"), released)
    }
}
