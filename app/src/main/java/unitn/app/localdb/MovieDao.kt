package unitn.app.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import unitn.app.api.Movies

@Dao
interface MovieDao {
    @Query("SELECT * FROM Movies")
    suspend fun getAll(): List<Movies>

    @Query("SELECT mediaId FROM Movies")
    suspend fun getAllId(): List<Int>

    @Query("SELECT * FROM Movies WHERE mediaId = :id")
    suspend fun getMovieFromId(id: Int): Movies

    @Insert
    suspend fun insertMovie(movie: Movies)

    @Query("DELETE FROM Movies WHERE mediaId = :id")
    suspend fun deleteMovie(id: Int)


    @Query("UPDATE Movies SET isLocal=:isLocal WHERE mediaId = :id")
    suspend fun saveInLocal(id: Int, isLocal: Boolean)
}