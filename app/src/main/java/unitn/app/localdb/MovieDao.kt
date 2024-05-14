package unitn.app.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import unitn.app.api.Movies

@Dao
interface MovieDao {
    @Query("SELECT * FROM Movies")
    suspend fun getAll(): List<Movies>

    @Query("SELECT * FROM Movies WHERE ID = :id")
    suspend fun getMovieFromId(id: Int): Movies

    @Insert
    suspend fun insertMovie(movie: Movies)

    @Query("DELETE FROM Movies WHERE ID = :id")
    suspend fun deleteMovie(id: Int)
}