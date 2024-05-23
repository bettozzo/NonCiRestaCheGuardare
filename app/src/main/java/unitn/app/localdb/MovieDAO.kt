package unitn.app.localdb

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import unitn.app.api.LocalDbMedia

@Dao
interface MovieDAO {
    @Query("SELECT * FROM LocalDbMedia")
    suspend fun getAll(): List<LocalDbMedia>

    @Query("SELECT * FROM LocalDbMedia WHERE isFilm=1")
    suspend fun getAllMovies(): List<LocalDbMedia>

    @Query("SELECT * FROM LocalDbMedia WHERE isFilm=0")
    suspend fun getAllSeries(): List<LocalDbMedia>

    @Query("SELECT mediaId FROM LocalDbMedia")
    suspend fun getAllId(): List<Int>

    @Query("SELECT * FROM LocalDbMedia WHERE mediaId = :id")
    suspend fun getMovieFromId(id: Int): LocalDbMedia

    @Upsert
    suspend fun insertMedia(localDbMedia: LocalDbMedia)

    @Query("DELETE FROM LocalDbMedia WHERE mediaId = :id")
    suspend fun deleteMedia(id: Int)


    @Query("DELETE FROM LocalDbMedia")
    suspend fun deleteEveryMedia()

    @Query("UPDATE LocalDbMedia SET isLocal=:isLocal WHERE mediaId = :id")
    suspend fun saveInLocal(id: Int, isLocal: Boolean)
}