package unitn.app.remotedb

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.test.R
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import unitn.app.ConverterMedia
import unitn.app.api.LocalMedia
import unitn.app.api.MediaDetails
import unitn.app.localdb.UserDatabase
import java.time.LocalDate
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


    /*--------------------------*/
    /*-------Single Media-------*/
    /*--------------------------*/
    private suspend fun insertMedia(media: Media) {
        supabase.from("Media").insert(media)

        if (media.mediaID > 2147383646) {
            //is Custom
            deleteId(media.mediaID)
        }
    }

    private suspend fun getMedia(id: Int): Media? {
        return supabase.from("Media").select {
            filter {
                eq("mediaID", id)
            }
        }.decodeSingleOrNull<Media>();
    }

    private suspend fun deleteMedia(id: Int) {
        supabase.from("Media").delete {
            filter {
                eq("mediaID", id)
            }
        };
    }

    private suspend fun isMediaPresent(id: Int): Boolean {
        return getMedia(id) != null;
    }


    /*--------------------------*/
    /*-------Piattaforme--------*/
    /*--------------------------*/
    private suspend fun insertPiattaforma(nome: String, mediaId: Int) {
        val piattaformaInfo = supabase.from("Piattaforme").select {
            filter {
                eq("nome", nome)
            }
        }.decodeSingleOrNull<Piattaforme>() ?: return;

        insertDoveVedereMedia(mediaId, piattaformaInfo.nome)
    }


    /*--------------------------*/
    /*---------Watchlist--------*/
    /*--------------------------*/
    suspend fun insertToWatchlist(media: LocalMedia, appCompatActivity: AppCompatActivity) {
        if (!isMediaPresent(media.mediaId)) {
            insertMedia(ConverterMedia.toRemote(media))
            val piattaforme = media.platform;
            for (piattaforma in piattaforme) {
                insertPiattaforma(piattaforma.first, media.mediaId)
            }
        }

        maybeReloadDoveVedereMedia(media.mediaId, appCompatActivity)

        if (!isInWatchList(media.mediaId)) {
            supabase.from("watchlist").insert(InsertWatchListParams(user.userId, media.mediaId))
        }
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

    suspend fun deleteFromWatchList(mediaID: Int) {
        supabase.from("watchlist").delete {
            filter {
                eq("mediaid", mediaID)
                eq("userid", user.userId)
            }
        }

        if (mediaID > 2147383646) {
            //is Custom
            deleteMedia(mediaID)
        }
    }

    suspend fun getWatchList(): List<Pair<Media, Boolean>> {
        val columns = Columns.list(WatchList.getStructure())
        val result = supabase.from("watchlist").select(columns = columns) {
            filter {
                eq("userid", user.userId)

            }
            order(column = "id", order = Order.ASCENDING)
        }
        val list = result.decodeList<WatchList>()
        return list.map { Pair(it.mediaid, it.is_local) }
    }

    suspend fun changeIsLocal(mediaId: Int, newState: Boolean) {
        supabase.from("watchlist").update({ set("is_local", newState) }) {
            filter {
                eq("userid", user.userId)
                eq("mediaid", mediaId)
            }
        }
    }

    /*--------------------------*/
    /*--------Dove Vedere-------*/
    /*--------------------------*/
    suspend fun getDoveVedereMedia(
        mediaID: Int,
        appCompatActivity: AppCompatActivity,
    ): List<Piattaforme> {

        val columns = Columns.raw(DoveVedereMedia.getStructure())
        val doveVedereMedia = supabase.from("DoveVedereMedia").select(columns = columns) {
            filter {
                eq("mediaID", mediaID)
            }
        }.decodeList<DoveVedereMedia>()
        maybeReloadDoveVedereMedia(mediaID, appCompatActivity)

        val piattaforme = doveVedereMedia.map { it.piattaforma };
        return piattaforme
    }

    private suspend fun maybeReloadDoveVedereMedia(
        mediaID: Int,
        appCompatActivity: AppCompatActivity,
    ) {
        val columns = Columns.raw(DoveVedereMedia.getStructure())
        val today = LocalDate.now()
        val doveVedereMedia = supabase.from("DoveVedereMedia").select(columns = columns) {
            filter {
                eq("mediaID", mediaID)
            }
        }.decodeList<DoveVedereMedia>()
        val lastUpdates = doveVedereMedia.map { LocalDate.parse(it.lastUpdate) }
        for (lastUpdate in lastUpdates) {
            val monthAfterUpdate = lastUpdate.plusMonths(1)
            if (today.isAfter(monthAfterUpdate)) {
                val media = getMedia(mediaID)!!
                deleteDoveVedereMedia(mediaID)
                val mediaDetails = ViewModelProvider(appCompatActivity)[MediaDetails::class.java];
                val platforms = mediaDetails.getMediaPlatform(
                    mediaID,
                    media.is_film,
                    appCompatActivity.resources.getString(R.string.api_key_tmdb)
                )
                for (piattaforma in platforms) {
                    insertPiattaforma(piattaforma.first, mediaID)
                }
            }
        }
    }

    private suspend fun insertDoveVedereMedia(mediaId: Int, nomePiattaforme: String) {
        supabase.from("DoveVedereMedia")
            .insert(InsertDoveVedereMediaParams(mediaId, nomePiattaforme))
    }

    private suspend fun deleteDoveVedereMedia(mediaId: Int) {
        supabase.from("DoveVedereMedia").delete {
            filter {
                eq("mediaID", mediaId)
            }
        }
    }


    /*--------------------------*/
    /*-----------Users----------*/
    /*--------------------------*/
    fun getMainColor(): Colori {
        return user.coloreTemaPrincipale
    }

    suspend fun updateColor(color: String) {
        supabase.from("Users").update({ set("coloreTemaPrincipale", color) }) {
            filter {
                eq("userId", user.userId)
            }
        }
    }

    suspend fun getDarkTheme(): Boolean {
        return supabase.from("Users").select(columns = Columns.raw(Users.getStructure())) {
            filter {
                eq("userId", user.userId)
            }
        }.decodeSingle<Users>().temaScuro
    }

    suspend fun updateDarkTheme(isOn: Boolean) {
        supabase.from("Users").update({ set("temaScuro", isOn) }) {
            filter {
                eq("userId", user.userId)
            }
        }
    }

    /*--------------------------*/
    /*--------Cronologia--------*/
    /*--------------------------*/
    suspend fun getCronologia(): List<Pair<Media, String>> {
        return supabase.from("CronologiaMedia")
            .select(columns = Columns.raw(CronologiaMedia.getStructure())) {
                filter {
                    eq("userid", user.userId)
                }
                order(column = "dataVisione", order = Order.DESCENDING)
            }.decodeList<CronologiaMedia>().map { Pair(it.mediaId, it.dataVisione) }
    }

    suspend fun insertToCronologia(mediaId: Int) {
        supabase.from("CronologiaMedia")
            .insert(InsertCronologiaMediaParams(user.userId, mediaId))

    }

    /*--------------------------*/
    /*----Piattaforme User------*/
    /*--------------------------*/
    suspend fun insertPiattaformaAdUser(piattaformaNome: String) {
        supabase.from("PiattaformeDiUser")
            .insert(InsertPiattaformeDiUsersParams(user.userId, piattaformaNome))
    }

    suspend fun removePiattaformaAdUser(piattaformaNome: String) {
        supabase.from("PiattaformeDiUser").delete {
            filter {
                eq("userId", user.userId)
                eq("piattaformaNome", piattaformaNome)
            }
        }
    }

    suspend fun getPiattaformeUser(): List<Piattaforme> {
        return supabase.from("PiattaformeDiUser")
            .select(columns = Columns.raw(PiattaformeDiUsers.getStructure())) {
                filter {
                    eq("userId", user.userId)
                }
            }.decodeList<PiattaformeDiUsers>().map { it.piattaformaNome }
    }

    /*--------------------------*/
    /*------Possibili ID--------*/
    /*--------------------------*/
    suspend fun getRandomHighID(): PossibiliID {
        val id = supabase.from("possibiliID")
            .select(columns = Columns.raw(PossibiliID.getStructure())) {
                limit(count = 1);
            }.decodeSingle<PossibiliID>()
        deleteId(id.possibiliID);
        return id;
    }

    private suspend fun deleteId(id: Int) {
        supabase.from("possibiliID")
            .delete {
                filter {
                    eq("possibiliID", id)
                }
            }
    }

    private suspend fun insertID(id: Int) {
        supabase.from("possibiliID")
            .insert(PossibiliID(id))
    }
}





