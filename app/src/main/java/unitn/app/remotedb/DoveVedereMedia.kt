package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class DoveVedereMedia(
    val id: Int,
    val mediaID: Int,
    val piattaforma: String,
) {

//    @Serializable
//    companion object {
//        fun getStructure(): String {
//            return "id," +
//                    Media.getStructure() + "," +
//                    Piattaforme.getStructure()
//                        .trimIndent()
//        }
//    }
}

