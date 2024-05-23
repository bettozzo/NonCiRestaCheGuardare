package unitn.app.remotedb

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
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

    suspend fun addMediaToWatchList(media: Media) {
        if (getMedia(media.mediaID) == null) {
            supabase.from("Media").insert(media)
        }
        supabase.postgrest.rpc("Insert_watchlist", watcherlist(user.userId, media.mediaID))
    }

    private suspend fun getMedia(id: Int): Media? {
        return supabase.from("Media").select() {
            filter {
                eq("mediaID", id)
            }
        }.decodeSingleOrNull<Media>();
    }

    suspend fun getWatchList(): List<Media> {
        val columns = Columns.list("mediaid("+Media.getStructure()+")")
        val result = supabase.from("watchlist").select(columns = columns) {
            filter {
                eq("userid", user.userId)
            }
        }.component1().replace("{\"mediaid\":", "").replace("}}", "}")
        return stringToWatcherList(result)
    }

    fun getMainColor(): Int {
        return Color.parseColor(user.coloreTemaPrincipale.colorCode)
    }

}


@Serializable
private class watcherlist(
    val useridarg: String,
    val mediaidarg: Int,
) {
    companion object {
        fun getStructure(): String {
            return "id," +
                    "userid(" +
                    Users.getStructure() +
                    ")," +
                    "mediaid(" +
                    Media.getStructure() +
                    ")"
                        .trimIndent()
        }
    }

    override fun toString(): String {
        return "(userid= $useridarg\tmediaIdArg= $mediaidarg)"
    }
}


private fun stringToWatcherList(data: String?): List<Media> {
    val gson = Gson()
    if (data == null) {
        return emptyList();
    }
    val type = object : TypeToken<List<Media>>() {}.type
    return gson.fromJson<List<Media>>(data, type)
}