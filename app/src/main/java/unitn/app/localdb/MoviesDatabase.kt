package unitn.app.localdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import unitn.app.api.Movies

@Database(entities = [Movies::class], version = 1)
@TypeConverters(Converters::class)
abstract class MoviesDatabase : RoomDatabase(){
    abstract fun movieDao(): MovieDao
}