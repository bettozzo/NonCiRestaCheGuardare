package unitn.app.api

data class CastClass(
    val adult: Boolean,
    val gender: Int,
    val id: Int,
    val known_for_department: String,
    val name: String,
    val original_name: String,
    val popularity: Float,
    val profile_path: String,
    val character: String,
    val credit_id: String,
    val order: Int,
)
data class CrewClass(
    val adult: Boolean,
    val gender: Int,
    val id: Int,
    val known_for_department: String,
    val name: String,
    val original_name: String,
    val popularity: Float,
    val profile_path: String,
    val credit_id: String,
    val department: String,
    val job: String,
)

data class CreditsResults(
    val id: Int,
    val cast: List<CastClass>,
    val crew: List<CrewClass>,
)
