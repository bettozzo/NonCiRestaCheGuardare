package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class PiattaformeDiUsers(
    val userId: Users,
    val piattaformaNome: Piattaforme,
) {
    companion object {
        fun getStructure(): String {
            return "userId(" +
                    Users.getStructure() + ")," +
                    "piattaformaNome(" +
                    Piattaforme.getStructure() + ")";
        }
    }
}

@Serializable
class InsertPiattaformeDiUsersParams(
    val userId: String,
    val piattaformaNome: String,
)