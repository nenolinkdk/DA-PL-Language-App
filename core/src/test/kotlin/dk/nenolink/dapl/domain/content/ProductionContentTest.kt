package dk.nenolink.dapl.domain.content

import dk.nenolink.dapl.domain.dialogue.DialogueValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionContentTest {
    private val library = CourseLibraryLoader.loadBundled()

    @Test
    fun level1LessonsAreReleasedWithPhrasesQuizAndNotes() {
        val titles = listOf(
            "Hej, goddag og høflighed",
            "Ja, nej, tak og undskyld",
            "Tal, priser og betaling",
            "På café",
            "Restaurant og mad",
            "Butik og indkøb",
            "Hotel og overnatning",
            "Tog, bus og billetter",
            "Vejvisning og steder",
            "Hjælp og enkle problemer"
        )
        titles.forEachIndexed { index, title ->
            val id = "lesson-${(index + 1).toString().padStart(2, '0')}"
            val lesson = library.lesson(id)!!
            assertEquals("level1", lesson.moduleId)
            assertEquals(title, lesson.title)
            assertTrue(lesson.released)
            assertTrue(LessonRules.errors(lesson).isEmpty())
            assertTrue(library.catalog.lesson(id)!!.hasQuiz)
            assertEquals(
                ScreenKind.LESSON_PLAYER,
                library.screenFor(dk.nenolink.dapl.domain.navigation.Destination.Lesson(id)).kind
            )
        }
        val level2 = library.catalog.module(dk.nenolink.dapl.domain.model.AppRoute.LEVEL2)!!
        assertEquals(10, level2.lessons.size)
        level2.lessons.forEach { entry ->
            val lesson = library.lesson(entry.id)!!
            assertEquals("level2", lesson.moduleId)
            assertEquals(entry.title.support, lesson.title)
            assertTrue(LessonRules.errors(lesson).isEmpty())
        }
        assertEquals("Cześć", library.lesson("lesson-01")!!.items.first { it.type == "phrase" }.polish)
    }

    @Test
    fun grammarSheetsKeepImportedExamples() {
        assertEquals(listOf("grammar-hello-politeness", "grammar-you-formal"), library.grammarSheets.map { it.id })
        val politeness = library.grammarSheets.first()
        assertEquals("Cześć!", politeness.sections.first().examples.first().polish)
        val screen = library.screenFor(dk.nenolink.dapl.domain.navigation.Destination.Grammar)
        assertEquals(ScreenKind.GRAMMAR, screen.kind)
    }

    @Test
    fun dialoguesKeepPolishLinesAndHiddenDanishSupport() {
        val cafe = library.dialogue("dlg-cafe-001")!!
        val ticket = library.dialogue("dlg-ticket-001")!!
        assertTrue(DialogueValidator.validate(cafe).errors.isEmpty())
        assertTrue(DialogueValidator.validate(ticket).errors.isEmpty())
        val opening = cafe.states.getValue("start")
        assertEquals("Dzień dobry! Co podać?", opening.spokenPolish())
        assertEquals("Goddag! Hvad må det være?", opening.support)
        assertEquals("Miłej podróży!", ticket.states.getValue("done").spokenPolish())
        library.dialogues.values.forEach { scenario ->
            scenario.states.values.forEach { state ->
                assertTrue(state.spokenPolish().isNotBlank())
                assertTrue(state.support.isNotBlank())
                assertTrue(state.spokenPolish() != state.support)
            }
        }
        assertEquals(ScreenKind.DIALOGUE, library.screenFor(dk.nenolink.dapl.domain.navigation.Destination.Scenario("dlg-cafe-001")).kind)
    }

    @Test
    fun preservedManifestIsNotTheMenuSource() {
        val manifest = CourseLibraryLoader::class.java.classLoader
            ?.getResourceAsStream("config/manifest.json")
            ?.bufferedReader(Charsets.UTF_8)
            ?.use { it.readText() }
            ?: error("manifest missing")
        assertTrue(manifest.contains("Niveau 1 - Basis"))
        assertEquals(8, library.menuEntries().size)
        assertFalse(library.menuEntries().any { it.label == "Børneside" })
    }
}
