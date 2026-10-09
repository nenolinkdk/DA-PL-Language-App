package dk.nenolink.dapl.data.content

import android.content.Context
import dk.nenolink.dapl.domain.content.CatalogLoader
import dk.nenolink.dapl.domain.model.CourseCatalog

/**
 * Reads the course index from app assets.
 * Lesson bodies and dialogue graphs are intentionally absent in Phase 1A.
 */
class AssetContentRepository(context: Context) {
    private val appContext = context.applicationContext

    fun loadCatalog(): CourseCatalog {
        val json = appContext.assets.open(ASSET_PATH).bufferedReader(Charsets.UTF_8).use { it.readText() }
        return CatalogLoader.load(json)
    }

    private companion object {
        const val ASSET_PATH = "course/catalog.json"
    }
}
