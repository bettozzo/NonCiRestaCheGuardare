package unitn.app.api

data class UnfilteredMediaDetails(
    var adult: Boolean,
    var backdrop_path: String?,
    val genre_ids: List<Int>,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Float,
    val poster_path: String?,
    val release_date: String,
    val title: String,
    val name: String,
    val vote_average: Float,
)

data class MediaResultsFromAPI(
    var page: Int,
    var results: List<UnfilteredMediaDetails>,
    var total_pages: Int,
    var total_results: Int,
)