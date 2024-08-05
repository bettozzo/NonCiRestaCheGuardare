package unitn.app.api

import kotlinx.serialization.Serializable

@Serializable
data class ResDetailsMovie(
    var adult: Boolean,
    var backdrop_path: String?,
    var belongs_to_collection: CollectionInfo,
    var budget: Int,
    val genres: List<Pair<Int, String>>?,
    val homepage: String?,
    val id: Int,
    val imdb_id: String?,
    val original_language: String?,
    val original_title: String?,
    val overview: String?,
    val popularity: Float?,
    val poster_path: String?,
    val production_companies:  List<ProductionCompaniesInfo>?,
    val production_countries:  List<Pair<String, String>>?,
    val release_date: String?,
    val revenue: Int,
    val runtime: Int,
    val spoken_languages: List<Triple<String, String, String>>?,
    val status: String,
    val tagline: String,
    val title: String?,
    val video: Boolean?,
    val vote_average: Float?,
    val vote_count: Int,
)

@Serializable
data class ResDetailsTvSeries(
    var adult: Boolean,
    var backdrop_path: String?,
    var created_by: List<CreatedByInfo>?,
    var episode_run_time: List<Int>,
    val first_air_date: String,
    val genres: List<Pair<Int, String>>?,
    val homepage: String?,
    val id: Int,
    val in_production: Boolean,
    val languages: List<String>,
    val last_air_date: String,
    val last_episode_to_air: LastEpisodeInfo,
    val name: String,
    val next_episode_to_air: LastEpisodeInfo,
    val networks: List<ProductionCompaniesInfo>?,
    val number_of_episodes: Int,
    val number_of_seasons: Int,
    val origin_country: List<String>,
    val original_language: String?,
    val original_name: String?,
    val overview: String?,
    val popularity: Float?,
    val poster_path: String?,
    val production_companies:  List<ProductionCompaniesInfo>?,
    val production_countries:  List<Pair<String, String>>?,
    val seasons:  List<SeasonsInfo>?,
    val spoken_languages: List<Triple<String, String, String>>?,
    val status: String,
    val tagline: String,
    val type: String?,
    val vote_average: Float?,
    val vote_count: Int,
)

@Serializable
class CollectionInfo(
    val id: Int,
    val name: String,
    val poster_path: String?,
    val backdrop_path: String?,
)


@Serializable
data class ProductionCompaniesInfo(
    val id: Int,
    val logo_path: String,
    val name: String,
    val origin_country: String
)


@Serializable
data class CreatedByInfo(
    val id: Int,
    val credit_id: String,
    val gender: Int,
    val profile_path: String
)

@Serializable
class LastEpisodeInfo(
    val id: Int,
    val name: String,
    val overview: String,
    val vote_average: Float?,
    val vote_count: Int,
    val air_date: String,
    val episode_number: Int,
    val production_code: String,
    val runtime: Int,
    val season_number: Int,
    val show_id: Int,
    val still_path:String,
)


@Serializable
class SeasonsInfo(
    val air_date: String,
    val episode_count: Int,
    val id: Int,
    val name: String,
    val overview: String,
    val poster_path: String,
    val season_number: Int,
    val vote_average: Float,
)