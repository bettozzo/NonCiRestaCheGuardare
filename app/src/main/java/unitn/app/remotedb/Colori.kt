package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class Colori(
    var colorName: String,
    var colorCode: String,
){

}


fun coloriGetStructure(): String {
    return """
              colorName,
              colorCode
            """.trimIndent()
}

