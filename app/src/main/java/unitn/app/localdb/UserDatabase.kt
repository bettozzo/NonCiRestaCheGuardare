package unitn.app.localdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import unitn.app.remotedb.Users


@Database(entities = [Users::class], version = 9)
@TypeConverters(unitn.app.remotedb.Converters::class)
abstract class UserDatabase : RoomDatabase(){
    abstract fun userDao(): UserDAO
}