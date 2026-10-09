package dk.nenolink.dapl.domain.progress

interface CourseProgress {
    fun setCompleted(key: String, completed: Boolean = true)
    fun isCompleted(key: String): Boolean
    fun completedKeys(): List<String>
}

class MemoryProgress : CourseProgress {
    private val flags = linkedMapOf<String, Boolean>()

    override fun setCompleted(key: String, completed: Boolean) {
        flags[key] = completed
    }

    override fun isCompleted(key: String): Boolean = flags[key] == true

    override fun completedKeys(): List<String> = flags.filterValues { it }.keys.sorted()
}
