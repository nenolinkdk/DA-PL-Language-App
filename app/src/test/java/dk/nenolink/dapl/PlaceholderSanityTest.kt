package dk.nenolink.dapl

import org.junit.Assert.assertEquals
import org.junit.Test

class PlaceholderSanityTest {

    @Test
    fun packageNameIsFrozen() {
        assertEquals("dk.nenolink.dapl", BuildConfigCheck.APPLICATION_ID)
    }
}

object BuildConfigCheck {
    const val APPLICATION_ID = "dk.nenolink.dapl"
}
