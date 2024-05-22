package unitn.app.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import unitn.app.remotedb.Users

@Dao
interface UserDAO {
    @Query("SELECT userId FROM Users")
    suspend fun getUserId(): String

    @Insert
    suspend fun inserUserId(user: Users)

    @Query("DELETE FROM USERS WHERE userId = :id")
    suspend fun deleteUserId(id: Int)
}