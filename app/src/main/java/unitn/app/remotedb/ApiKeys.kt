package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class ApiKeys(
    val sito: String,
    val value: String,
){
    companion object {
        fun getStructure(): String {
            return "sito, " +
                    "value"
        }
    }
}
