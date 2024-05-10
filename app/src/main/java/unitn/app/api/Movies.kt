package unitn.app.api

data class Movies(
    val id: Int,
    val title: String,
    val platform: List<String>,
    val posterPath: String
) {
    override fun toString(): String {
        return "[%07d] $title\n\tposter_url=$posterPath\n\tplatforms=$platform".format(id)
    }
}
