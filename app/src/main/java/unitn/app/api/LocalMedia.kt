package unitn.app.api

data class LocalMedia(
    val mediaId: Int,
    val isFilm: Boolean,
    val title: String,
    val platform: List<Pair<String, String>>,
    val posterPath: String?,
    val isLocallySaved: Boolean,
    val sinossi: String?,
    val cast: List<String>,
    val crew: List<String>,
    val databaseID: Int = 0,
) {
    override fun toString(): String {
        return "[$mediaId]$title"
    }
}
