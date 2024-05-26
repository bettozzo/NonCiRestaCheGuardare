package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class CronologiaMedia(
    val userid: Users,
    val mediaId: Media,
    val dataVisione: String
) {
    companion object {
        fun getStructure(): String {
            return "userid(" +
                    Users.getStructure() + ")," +
                    "mediaId(" +
                    Media.getStructure() +
                    "), " +
                    "dataVisione";
        }
    }
}

@Serializable
data class InsertCronologiaMediaParams(
    val userid: String,
    val mediaId: Int,
)