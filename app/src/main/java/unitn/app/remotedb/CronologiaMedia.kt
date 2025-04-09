package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class CronologiaMedia(
    val userid: Users,
    val mediaId: Media,
    val dataVisione: String,
    val rating: Float?,
    val maxRating: Float?,
    val recensione: String?
) {
    companion object {
        fun getStructure(): String {
            return "userid(" +
                    Users.getStructure() + ")," +
                    "mediaId(" +
                    Media.getStructure() +
                    "), " +
                    "dataVisione," +
                    "rating," +
                    "maxRating," +
                    "recensione";
        }
    }
}

data class CronologiaConRating(
    val media: Media,
    val dataVisione: String,
    val rating: Float?,
    val maxRating: Float?,
    val recensione: String?,
)

@Serializable
data class InsertCronologiaMediaParams(
    val userid: String,
    val mediaId: Int,
    val dataVisione: String,
    val rating: Float,
    val maxRating: Float,
    val recensione: String
)
