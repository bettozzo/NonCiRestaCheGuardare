package unitn.app.api

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Media(
    @ColumnInfo(name = "mediaId") val mediaId: Int,
    @ColumnInfo(name = "isFilm") val isFilm: Boolean,
    @ColumnInfo(name = "titolo") val title: String,
    @ColumnInfo(name = "platformsAndLogo") val platform: List<Pair<String, String>>,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "isLocal") val isLocallySaved: Boolean,
    @ColumnInfo(name = "sinossi") val sinossi: String?,
    @PrimaryKey(autoGenerate = true) val databaseID: Int = 0,
)