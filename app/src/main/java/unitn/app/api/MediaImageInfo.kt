package unitn.app.api

data class MovieImageInfo(
    val aspect_ratio: Float,
    val height: Int,
    val iso_639_1: String,
    val file_path: String,
    val vote_average: Float,
    val vote_count: Float,
    val width: Int
);

data class MediaResults(
    val backdrops: List<MovieImageInfo>?,
    val id: Int,
    val logo: List<MovieImageInfo>?,
    val posters: List<MovieImageInfo>?
)
