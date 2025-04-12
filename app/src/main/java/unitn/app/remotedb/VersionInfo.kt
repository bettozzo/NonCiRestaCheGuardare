package unitn.app.remotedb

import kotlinx.serialization.Serializable

@Serializable
data class VersionInfo(
    val major_version: Int,
    val minor_version: Int,
    val patch_version: Int,
) {
    override fun toString(): String {
        return this.major_version.toString() + "." +
                this.minor_version.toString() + "." +
                this.patch_version.toString();
    }
}
