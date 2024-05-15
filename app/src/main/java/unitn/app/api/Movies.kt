package unitn.app.api

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Movies(
    @ColumnInfo(name = "mediaId") val mediaId: Int,
    @ColumnInfo(name = "isFilm") val isFilm: Boolean,
    @ColumnInfo(name = "titolo") val title: String,
    @ColumnInfo(name = "platformsAndLogo") val platform: List<Pair<String, String>>,
    @ColumnInfo(name = "poster_path") val posterPath: String,
    @ColumnInfo(name = "isLocal") val isLocallySaved: Boolean,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) {
    override fun toString(): String {
        return "[%07d] $title\n\tposter_url=$posterPath\n\tplatforms=$platform\n".format(mediaId)
    }
}