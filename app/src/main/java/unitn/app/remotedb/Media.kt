package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val mediaID: Int,
    val is_film: Boolean,
    val titolo: String,
    val poster_path: String?,
    val sinossi: String,
) {
    companion object {
        fun getStructure(): String {
            return """
                mediaID,
                is_film,
                titolo,
                poster_path,
                sinossi
            """.trimIndent().replace("\n", "")
        }
    }

    override fun toString(): String {
        return "[$mediaID]$titolo";
    }
}
