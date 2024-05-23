package unitn.app.remotedb

import android.content.Context
import android.graphics.Color
import androidx.room.Room
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import unitn.app.localdb.UserDatabase
import kotlin.coroutines.CoroutineContext


class RemoteDAO(mContext: Context, override val coroutineContext: CoroutineContext) :
    CoroutineScope {
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://gxyzupxvwiuhyjtbbwmb.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd4eXp1cHh2d2l1aHlqdGJid21iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTYyOTkzMTMsImV4cCI6MjAzMTg3NTMxM30.4r45EIXsyGCFnsmyx9IcZPFF0NpxFuOrDvf4ghdgdEs"
    ) {
        install(Postgrest)
    }

    private var user: Users;

    init {
        val userDao = Room.databaseBuilder(
            mContext,
            UserDatabase::class.java, "user-db"
        ).addTypeConverter(Converters())
            .build().userDao()

        runBlocking {
            val userId = userDao.getUserId();

            val columns = Columns.raw(Users.getStructure())
            user = supabase.from("Users").select(columns = columns) {
                filter {
                    eq("userId", userId)
                }
            }.decodeSingle<Users>()
        }
    }

    private suspend fun insertMedia(media: Media) {
        supabase.from("Media").insert(media)
    }

    private suspend fun getMedia(id: Int): Media? {
        return supabase.from("Media").select {
            filter {
                eq("mediaID", id)
            }
        }.decodeSingleOrNull<Media>();
    }

    private suspend fun alreadyInWatchList(id: Int): Boolean {
        val mediaInString = supabase.from("watchlist").select {
            filter {
                eq("mediaid", id)
                eq("userid", user.userId)
            }
        }.component1();
        return mediaInString != "[]"
    }

    suspend fun insertToWatchlist(media: Media) {
        if (getMedia(media.mediaID) == null) {
            insertMedia(media)
        }

        if(!alreadyInWatchList(media.mediaID)) {
            supabase.from("watchlist").insert(InsertWatchListParams(user.userId, media.mediaID))
        }
    }

    suspend fun deleteFromWatchList(mediaID: Int){
        supabase.from("watchlist").delete {
            filter{
                eq("mediaid", mediaID)
                eq("userid", user.userId)
            }
        }
    }
    suspend fun getWatchList(): List<Media> {
        val columns = Columns.list(WatchList.getStructure())
        val result = supabase.from("watchlist").select(columns = columns) {
            filter {
                eq("userid", user.userId)
            }
        }
        val list = result.decodeList<WatchList>()
        return list.map { it.mediaid }
    }

    fun getMainColor(): Int {
        return Color.parseColor(user.coloreTemaPrincipale.colorCode)
    }

}





