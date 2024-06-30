package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class VersionInfo(
    val version: Int,
)
