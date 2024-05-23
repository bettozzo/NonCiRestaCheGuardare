package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class DoveVedereMedia(
    val id: Int,
    val mediaID: Media,
    val piattaforma: Piattaforme,
) {
    companion object {
        fun getStructure(): String {
            return "id," +
                    "mediaID(" +
                    Media.getStructure() + ")," +
                    "piattaforma(" +
                    Piattaforme.getStructure()+ ")"
        }
    }
}


@Serializable
class InsertDoveVedereMediaParams(
    val mediaID: Int,
    val piattaforma: String,
)