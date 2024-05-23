package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class WatchList(
    val id: Int,
    val userid: Users,
    val mediaid: Media,
) {

    companion object {
        fun getStructure(): String {
            return "id," +
                    "userid(" +
                    Users.getStructure() + ")," +
                    "mediaid(" +
                    Media.getStructure() + ")"
                .trimIndent()
        }
    }
}

@Serializable
class InsertWatchListParams(
    val useridarg: String,
    val mediaidarg: Int,
)