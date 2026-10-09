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
    private val catalog = CatalogLoader.loadBundled()

    @Test
    fun applicationIdAndLocalesStayOnTheFrozenContract() {
        assertEquals("dk.nenolink.dapl", AppIdentity.APPLICATION_ID)
        assertEquals("da-DK", AppIdentity.SUPPORT_LOCALE)
        assertEquals("pl-PL", AppIdentity.TARGET_LOCALE)
    }

    @Test
    fun menuListsTheEightStructuralModulesInOrder() {
        val entries = catalog.menuEntries()
        assertEquals(AppRoute.menuOrder, entries.map { it.route })
        assertTrue(entries.all { it.label.isNotBlank() && it.detail.isNotBlank() })
    }

    @Test
    fun level1IndexesTenUnreleasedLessonsWithoutPolishPhrases() {
        val level1 = catalog.module(AppRoute.LEVEL1)!!
        assertEquals(ModuleAvailability.STRUCTURE_READY, level1.availability)
        assertEquals(10, level1.lessons.size)
        assertTrue(level1.lessons.all { !it.released })
        assertTrue(level1.lessons.all { it.title.support.isNotBlank() && it.title.target.isBlank() })

        val screen = catalog.screenFor(Destination.Level1)
        assertEquals(ScreenKind.LESSON_LIST, screen.kind)
        assertEquals(level1.lessons.map { it.id }, screen.lessons.map { it.id })
        assertTrue(screen.lessons.all { !it.released && it.statusLabel == "Ikke udgivet endnu" })
        assertTrue(screen.body.isNotBlank())
    }

    @Test
    fun level2AndLevel3AreNotYetFilledAndDoNotCrash() {
        listOf(AppRoute.LEVEL2, AppRoute.LEVEL3).forEach { route ->
            val module = catalog.module(route)!!
            assertEquals(ModuleAvailability.NOT_YET_FILLED, module.availability)
            assertTrue(module.lessons.isEmpty())
            val screen = catalog.screenFor(route.toDestination())
            assertEquals(ScreenKind.UNAVAILABLE, screen.kind)
            assertTrue(screen.title.isNotBlank())
            assertTrue(screen.body.isNotBlank())
            assertTrue(screen.lessons.isEmpty())
            assertTrue(screen.scenarios.isEmpty())
        }
    }

    @Test
    fun unreleasedLessonScreenIsASafePlaceholder() {
        val lessonId = "lesson-l1-01-greetings"
        val screen = catalog.screenFor(Destination.Lesson(lessonId))
        assertEquals(ScreenKind.UNAVAILABLE, screen.kind)
        assertEquals("Hej, goddag og høflighed", screen.title)
        assertTrue(screen.body.contains("ikke udgivet"))
        assertTrue(screen.lessons.isEmpty())
    }

    @Test
    fun missingLessonScreenIsStillSafe() {
        val screen = catalog.screenFor(Destination.Lesson("lesson-missing"))
        assertEquals(ScreenKind.UNAVAILABLE, screen.kind)
        assertTrue(screen.body.isNotBlank())
    }

    @Test
    fun conversationQuizGrammarChildrenAndAboutHaveExplicitEmptyOrInfoCopy() {
        val conversation = catalog.screenFor(Destination.Conversation)
        assertEquals(ScreenKind.CONVERSATION, conversation.kind)
        assertTrue(conversation.scenarios.isEmpty())
        assertTrue(conversation.body.isNotBlank())

        listOf(Destination.Quiz, Destination.Grammar, Destination.Children).forEach { destination ->
            val screen = catalog.screenFor(destination)
            assertEquals(ScreenKind.UNAVAILABLE, screen.kind)
            assertTrue(screen.body.isNotBlank())
        }

        val about = catalog.screenFor(Destination.About)
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
    fun bundledCatalogHasNoDialogueScenarios() {
        assertTrue(catalog.scenarios.isEmpty())
        assertFalse(catalog.modules.flatMap { it.lessons }.any { it.released })
    }
}
