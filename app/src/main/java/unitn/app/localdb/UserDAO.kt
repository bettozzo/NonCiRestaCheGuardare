package unitn.app.localdb

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UserDAO {
    @Query("SELECT userId FROM Users")
    suspend fun getUserId(): String?

    @Query("INSERT INTO Users VALUES(:userid, 'Verde', 0)")
    suspend fun insertUser(userid: String)

    @Query("DELETE FROM Users WHERE userId = :id")
    suspend fun deleteUser(id: Int)

    @Query("DELETE FROM Users")
    suspend fun deleteEvertyhing()
}