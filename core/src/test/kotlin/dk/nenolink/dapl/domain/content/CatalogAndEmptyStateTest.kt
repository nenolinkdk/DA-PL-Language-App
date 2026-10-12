package dk.nenolink.dapl.domain.content

import dk.nenolink.dapl.domain.model.AppIdentity
import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.domain.model.ModuleAvailability
import dk.nenolink.dapl.domain.navigation.Destination
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
    fun availableLevelsShowCompletionOnTheMenu() {
        val fresh = library.menuEntries()
        assertEquals("0/10 gennemført", fresh.first { it.route == AppRoute.LEVEL1 }.detail)
        assertEquals("0/10 gennemført", fresh.first { it.route == AppRoute.LEVEL2 }.detail)
        assertEquals("Strukturen findes · indhold er ikke klar", fresh.first { it.route == AppRoute.LEVEL3 }.detail)
        assertEquals("Samtaletræning", fresh.first { it.route == AppRoute.CONVERSATION }.label)

        val started = library.menuEntries(setOf("lesson-01", "lesson-l2-01-routine", "not-a-lesson"))
        assertEquals("1/10 gennemført", started.first { it.route == AppRoute.LEVEL1 }.detail)
        assertEquals("1/10 gennemført", started.first { it.route == AppRoute.LEVEL2 }.detail)
    }

    @Test
    fun level1ListsTenReleasedLessons() {
        val level1 = catalog.module(AppRoute.LEVEL1)!!
        assertEquals(ModuleAvailability.STRUCTURE_READY, level1.availability)
        assertEquals(10, level1.lessons.size)
        assertEquals("lesson-01", level1.lessons.first().id)
        assertEquals("Hej, goddag og høflighed", level1.lessons.first().title.support)
        assertTrue(level1.lessons.all { it.released && it.title.target.isBlank() && it.hasQuiz })

        val screen = library.screenFor(Destination.Level1)
        assertEquals(ScreenKind.LESSON_LIST, screen.kind)
        assertEquals(level1.lessons.map { it.id }, screen.lessons.map { it.id })
        assertTrue(screen.lessons.all { it.released && it.statusLabel == "Klar" })
        assertTrue(screen.body.isNotBlank())
    }

    @Test
    fun level2ListsReleasedLessonsAndLevel3StaysEmpty() {
        val level2 = catalog.module(AppRoute.LEVEL2)!!
        assertEquals(ModuleAvailability.STRUCTURE_READY, level2.availability)
        assertEquals(10, level2.lessons.size)
        assertTrue(level2.lessons.all { it.released && it.hasQuiz })
        val screen = library.screenFor(Destination.Level2)
        assertEquals(ScreenKind.LESSON_LIST, screen.kind)
        assertEquals(level2.lessons.map { it.id }, screen.lessons.map { it.id })

        val level3 = catalog.module(AppRoute.LEVEL3)!!
        assertEquals(ModuleAvailability.NOT_YET_FILLED, level3.availability)
        assertTrue(level3.lessons.isEmpty())
        val empty = library.screenFor(Destination.Level3)
        assertEquals(ScreenKind.UNAVAILABLE, empty.kind)
        assertTrue(empty.body.isNotBlank())
    }

    @Test
    fun releasedLevel1LessonOpensAsAPlayer() {
        val screen = library.screenFor(Destination.Lesson("lesson-02"))
        assertEquals(ScreenKind.LESSON_PLAYER, screen.kind)
        assertEquals("Ja, nej, tak og undskyld", screen.title)
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
        assertEquals(
            listOf(
                "dlg-cafe-001",
                "dlg-ticket-001",
                "dlg-way-001",
                "dlg-hotel-001",
                "dlg-shop-001",
                "dlg-help-001"
            ),
            conversation.scenarios.map { it.id }
        )
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
    fun bundledCatalogReleasesBothLevelsAndSixScenarios() {
        assertEquals(
            listOf(
                "dlg-cafe-001",
                "dlg-ticket-001",
                "dlg-way-001",
                "dlg-hotel-001",
                "dlg-shop-001",
                "dlg-help-001"
            ),
            catalog.scenarios.map { it.id }
        )
        val released = catalog.modules.flatMap { it.lessons }.filter { it.released }
        assertEquals(20, released.size)
        assertTrue(released.all { it.hasQuiz })
    }
}
