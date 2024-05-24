package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class CronologiaMedia(
    val userid: Users,
    val mediaId: Media,
) {
    companion object {
        fun getStructure(): String {
            return "userid(" +
                    Users.getStructure() + ")," +
                    "mediaId(" +
                    Media.getStructure() +
                    ")";
        }
    }
}

@Serializable
data class InsertCronologiaMediaParams(
    val userid: String,
    val mediaId: Int,
) {
}