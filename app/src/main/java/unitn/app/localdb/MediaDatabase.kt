package unitn.app.localdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import unitn.app.api.LocalDbMedia

@Database(entities = [LocalDbMedia::class], version = 10)
@TypeConverters(Converters::class)
abstract class MediaDatabase : RoomDatabase(){
    abstract fun MediaDao(): MovieDAO
}