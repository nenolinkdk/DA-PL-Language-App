package dk.nenolink.dapl.domain.content

import dk.nenolink.dapl.domain.dialogue.DialogueValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionContentTest {
    private val library = CourseLibraryLoader.loadBundled()

    @Test
    fun lessonOneKeepsTheImportedPhrases() {
        val lesson = library.lesson("lesson-01")!!
        assertTrue(lesson.released)
        assertEquals("level1", lesson.moduleId)
        assertEquals("Basale hilsner", lesson.title)
        assertEquals(
            listOf(
                "Cześć",
                "Dzień dobry",
                "Dzień dobry",
                "Dobry wieczór",
                "Do widzenia",
                "Cześć",
                "Dziękuję",
                "Proszę",
                "Przepraszam",
                "Nie ma za co"
            ),
            lesson.items.map { it.polish }
        )
        assertEquals(5, lesson.quiz.size)
        assertTrue(library.catalog.lesson("lesson-01")!!.hasQuiz)
    }

    @Test
    fun lessonsTwoThroughTenStayUnreleasedAndEmpty() {
        (2..10).forEach { index ->
            val id = "lesson-${index.toString().padStart(2, '0')}"
            val lesson = library.lesson(id)!!
            assertFalse(lesson.released)
            assertTrue(lesson.items.isEmpty())
            assertTrue(lesson.quiz.isEmpty())
            assertFalse(library.catalog.lesson(id)!!.hasQuiz)
            val screen = library.screenFor(dk.nenolink.dapl.domain.navigation.Destination.Lesson(id))
            assertEquals(ScreenKind.UNAVAILABLE, screen.kind)
        }
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
