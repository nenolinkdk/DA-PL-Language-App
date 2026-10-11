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
    fun importedDialoguesValidateAndKeepTheirPrompts() {
        val cafe = library.dialogue("dlg-cafe-001")!!
        val ticket = library.dialogue("dlg-ticket-001")!!
        assertTrue(DialogueValidator.validate(cafe).errors.isEmpty())
        assertTrue(DialogueValidator.validate(ticket).errors.isEmpty())
        assertEquals(
            "Bartenderen nikker til dig. Han siger: 'Dzień dobry! Co podać?'",
            cafe.states.getValue("start").prompt
        )
        assertTrue(ticket.states.getValue("done").prompt.contains("Udowa życzenia!"))
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
