package unitn.app.api

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Movies(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "titolo") val title: String,
    @ColumnInfo(name = "platformsAndLogo") val platform: List<Pair<String, String>>,
    @ColumnInfo(name = "poster_path")val posterPath: String
) {
    override fun toString(): String {
        return "[%07d] $title\n\tposter_url=$posterPath\n\tplatforms=$platform".format(id)
    }
}
