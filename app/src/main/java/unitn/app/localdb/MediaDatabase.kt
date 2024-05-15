package unitn.app.localdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import unitn.app.api.Media

@Database(entities = [Media::class], version = 3)
@TypeConverters(Converters::class)
abstract class MediaDatabase : RoomDatabase(){
    abstract fun MediaDao(): MovieDao
}