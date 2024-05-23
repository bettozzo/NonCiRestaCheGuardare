package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val mediaID: Int,
    val is_film: Boolean,
    val titolo: String,
    val poster_path: String?,
    val is_local: Boolean,
    val sinossi: String,
) {
    companion object {
        fun getStructure(): String {
            return """
                mediaID,
                is_film,
                titolo,
                poster_path,
                is_local,
                sinossi
            """.trimIndent().replace("\n", "")
        }
    }
}
