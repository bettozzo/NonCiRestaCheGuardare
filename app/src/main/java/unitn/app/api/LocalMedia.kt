package unitn.app.api

data class LocalMedia(
    val mediaId: Int,
    val isFilm: Boolean,
    val title: String,
    val platform: List<Pair<String, String>>,
    val posterPath: String?,
    var isLocallySaved: Boolean,
    val sinossi: String?,
    val annoUscita: String?,
    val generi: String?,
    val durata: String?,
    val cast: List<Pair<String, String>> = emptyList(),
    val crew: List<Pair<String, String>> = emptyList(),
    var note: String? = null,
    val databaseID: Int = 0,
) {
    override fun toString(): String {
        return "$title, $platform\n"
    }
}
