package unitn.app.api

data class QueriedMovie(
    var adult: Boolean,
    var backdrop_path: String,
    val genre_ids: List<Int>,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Float,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val vote_average: Float,
)

data class MovieResult(
    var page: Int,
    var results: List<QueriedMovie>,
    var total_pages: Int,
    var total_results: Int,
)