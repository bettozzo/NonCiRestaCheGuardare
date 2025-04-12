package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class WatchList(
    val id: Int,
    val userid: Users,
    val mediaid: Media,
    val is_local: Boolean,
    val note: String?,
    val data_aggiunta: String,
) {
    companion object {
        fun getStructure(): String {
            return "id," +
                    "userid(" +
                    Users.getStructure() + ")," +
                    "mediaid(" +
                    Media.getStructure() + ")," +
                    "is_local," +
                    "note," +
                    "data_aggiunta"
        }
    }
}

@Serializable
class InsertWatchListParams(
    val userid: String,
    val mediaid: Int,
    val data_aggiunta: String,
)
