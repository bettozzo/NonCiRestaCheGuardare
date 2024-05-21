package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class CustomColors(
    var colorName: String,
    var colorCode: String,
)

