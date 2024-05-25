package unitn.app.remotedb

import android.content.Context
import androidx.room.Room
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import unitn.app.ConverterMedia
import unitn.app.api.LocalMedia
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
        ).build().userDao()

        runBlocking {
            val userId = userDao.getUserId()!!;

            user = initUser(userId)!!
        }
    }

    companion object {
        private val supabase = createSupabaseClient(
            supabaseUrl = "https://gxyzupxvwiuhyjtbbwmb.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd4eXp1cHh2d2l1aHlqdGJid21iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTYyOTkzMTMsImV4cCI6MjAzMTg3NTMxM30.4r45EIXsyGCFnsmyx9IcZPFF0NpxFuOrDvf4ghdgdEs"
        ) {
            install(Postgrest)
        }

        suspend fun initUser(userid: String): Users? {
            val columns = Columns.raw(Users.getStructure())
            return supabase.from("Users").select(columns = columns) {
                filter {
                    eq("userId", userid)
                }
            }.decodeSingleOrNull<Users>()
        }

        suspend fun insertUser(userid: String) {
            supabase.from("Users").insert(InsertUsersParams(userid))
        }


        suspend fun deleteUser(userid: String) {
            supabase.from("Users").delete {
                filter {
                    eq("userId", userid)
                }
            }
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

    private suspend fun isMediaPresent(id: Int): Boolean {
        return getMedia(id) != null;
    }

    private suspend fun isInWatchList(id: Int): Boolean {
        val mediaInString = supabase.from("watchlist").select {
            filter {
                eq("mediaid", id)
                eq("userid", user.userId)
            }
        }.component1();
        return mediaInString != "[]"
    }

    private suspend fun insertPiattaforma(nome: String, mediaId: Int) {
        val piattaformaInfo = supabase.from("Piattaforme").select {
            filter {
                eq("nome", nome)
            }
        }.decodeSingleOrNull<Piattaforme>() ?: return;


        supabase.from("DoveVedereMedia")
            .insert(InsertDoveVedereMediaParams(mediaId, piattaformaInfo!!.nome))
    }

    suspend fun insertToWatchlist(media: LocalMedia) {
        if (!isMediaPresent(media.mediaId)) {
            insertMedia(ConverterMedia.toRemote(media))
            val piattaforme = media.platform;
            for (piattaforma in piattaforme) {
                insertPiattaforma(piattaforma.first, media.mediaId)
            }
        }

        if (!isInWatchList(media.mediaId)) {
            supabase.from("watchlist").insert(InsertWatchListParams(user.userId, media.mediaId))
        }
    }

    suspend fun deleteFromWatchList(mediaID: Int) {
        supabase.from("watchlist").delete {
            filter {
                eq("mediaid", mediaID)
                eq("userid", user.userId)
            }
        }
    }

    suspend fun getWatchList(): List<Pair<Media, Boolean>> {
        val columns = Columns.list(WatchList.getStructure())
        val result = supabase.from("watchlist").select(columns = columns) {
            filter {
                eq("userid", user.userId)
            }
        }
        val list = result.decodeList<WatchList>()
        return list.map { Pair(it.mediaid, it.is_local) }
    }

    suspend fun getDoveVedereMedia(mediaID: Int): List<Piattaforme> {

        val columns = Columns.raw(DoveVedereMedia.getStructure())
        val doveVedereMedia = supabase.from("DoveVedereMedia").select(columns = columns) {
            filter {
                eq("mediaID", mediaID)
            }
        }.decodeList<DoveVedereMedia>()

        val piattaforme = doveVedereMedia.map { it.piattaforma };
        return piattaforme
    }

    fun getMainColor(): String {
        return user.coloreTemaPrincipale.colorCode
    }

    suspend fun insertColor(color: String) {
        supabase.from("Users").update({
            set("coloreTemaPrincipale", color)
        }) {
            filter {
                eq("userId", user.userId)
            }
        }
    }

    suspend fun changeIsLocal(mediaId: Int, newState: Boolean) {
        supabase.from("watchlist").update({
            set("is_local", newState)
        }) {
            filter {
                eq("userid", user.userId)
                eq("mediaid", mediaId)
            }
        }
    }

    suspend fun getAllSeenMedia(): List<Media> {
        return supabase.from("CronologiaMedia").select(Columns.raw(CronologiaMedia.getStructure())) {
            filter {
                eq("userid", user.userId)
            }
        }.decodeList<CronologiaMedia>().map { it.mediaId }
    }
    suspend fun insertToSeen(mediaId: Int) {
        supabase.from("CronologiaMedia").insert(InsertCronologiaMediaParams(user.userId, mediaId))
    }


}





