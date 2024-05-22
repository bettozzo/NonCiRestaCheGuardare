package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class WatchList(
    val id: Int,
    val userID: String,
    val mediaID: String,
) {

//    companion object {
//        fun getStructure(): String {
//            return "id," +
//                    Users.getStructure() + "," +
//                    Media.getStructure()
//                        .trimIndent()
//        }
//    }
}
