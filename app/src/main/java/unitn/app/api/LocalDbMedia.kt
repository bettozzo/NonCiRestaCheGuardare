package unitn.app.api

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import unitn.app.remotedb.Media

@Entity(primaryKeys = ["mediaId", "databaseId"])
data class LocalDbMedia(
     @ColumnInfo(name = "mediaId") val mediaId: Int,
    @ColumnInfo(name = "isFilm") val isFilm: Boolean,
    @ColumnInfo(name = "titolo") val title: String,
    @ColumnInfo(name = "platformsAndLogo") val platform: List<Pair<String, String>>,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "isLocal") val isLocallySaved: Boolean,
    @ColumnInfo(name = "sinossi") val sinossi: String?,
    @ColumnInfo(name = "databaseId") val databaseID: Int = 0,
)

class ConverterMediaToMedia(){
    fun toRemote(media: LocalDbMedia): Media{
        return Media(media.mediaId, media.isFilm, media.title, media.posterPath!!, media.isLocallySaved, media.sinossi!!)
    }
    fun toLocal(media: Media): LocalDbMedia{
        //TODO remove emptylist()
        return LocalDbMedia(media.mediaID, media.is_film, media.titolo, emptyList(),media.poster_path, media.is_local, media.sinossi)
    }
}