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
            return "mediaID," +
                    "is_film," +
                    "titolo," +
                    "poster_path," +
                    "sinossi"
        }
    }
}


@Serializable
data class AllDetailsMedia(
    val userid: String,
    val mediaid: Int,
    val is_film: Boolean,
    val titolo: String,
    val poster_path: String?,
    val sinossi: String,
    val is_local: Boolean,
    val nome: String?,
    val logo_path: String?,
    val lastupdate: String?,
) {
    companion object {
        fun getStructure(): String {
            return "userid," +
                    "mediaid," +
                    "is_film," +
                    "titolo," +
                    "poster_path," +
                    "sinossi,"+
                    "is_local," +
                    "nome," +
                    "logo_path,"+
                    "lastupdate"
        }
    }
}