package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class Piattaforme(
    val nome: String,
    val logo_path: String,
) {
    companion object {
        fun getStructure(): String {
            return "nome, logo_path";
        }
    }
}
