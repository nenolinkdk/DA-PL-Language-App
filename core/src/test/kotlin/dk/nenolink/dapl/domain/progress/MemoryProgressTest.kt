package dk.nenolink.dapl.domain.progress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryProgressTest {
    @Test
    fun completedFlagsRoundTripAndDoNotStoreADialogueTurn() {
        val progress = MemoryProgress()
        assertFalse(progress.isCompleted("lesson-01"))
        progress.setCompleted("lesson-01")
        progress.setCompleted("dlg-cafe-001")
        assertEquals(listOf("dlg-cafe-001", "lesson-01"), progress.completedKeys())
        assertTrue(progress.isCompleted("lesson-01"))
        progress.setCompleted("lesson-01", completed = false)
        assertFalse(progress.isCompleted("lesson-01"))
    }
}
