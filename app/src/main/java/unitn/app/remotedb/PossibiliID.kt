package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class PossibiliID(
    val possibiliID: Int,
){
    companion object {
        fun getStructure(): String {
            return "possibiliID";
        }
    }
}
