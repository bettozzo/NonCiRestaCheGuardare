package unitn.app.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import unitn.app.api.Media

@Dao
interface MovieDao {
    @Query("SELECT * FROM Media")
    suspend fun getAll(): List<Media>

    @Query("SELECT * FROM Media WHERE isFilm=1")
    suspend fun getAllMovies(): List<Media>

    @Query("SELECT * FROM Media WHERE isFilm=0")
    suspend fun getAllSeries(): List<Media>

    @Query("SELECT mediaId FROM Media")
    suspend fun getAllId(): List<Int>

    @Query("SELECT * FROM Media WHERE mediaId = :id")
    suspend fun getMovieFromId(id: Int): Media

    @Insert
    suspend fun insertMedia(media: Media)

    @Query("DELETE FROM Media WHERE mediaId = :id")
    suspend fun deleteMedia(id: Int)

    @Query("UPDATE Media SET isLocal=:isLocal WHERE mediaId = :id")
    suspend fun saveInLocal(id: Int, isLocal: Boolean)
}