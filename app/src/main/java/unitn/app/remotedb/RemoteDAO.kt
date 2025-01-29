package unitn.app.remotedb

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unitn.app.activities.ConverterMedia
import unitn.app.activities.LiveDatas
import unitn.app.activities.homepage.AdapterHomepage
import unitn.app.api.LocalMedia
import unitn.app.api.MediaDetails
import unitn.app.localdb.UserDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private var apiKeyTMDB: String;

    init {
        val userDao = Room.databaseBuilder(
            mContext,
            UserDatabase::class.java, "user-db"
        ).build().userDao()

        runBlocking {
            val userId = userDao.getUserId()!!;
            user = getUser(userId)!!

            val columns = Columns.raw(ApiKeys.getStructure())
            val apiKeys = supabase.from("ApiKeys").select(columns = columns) {
                filter {
                    eq("sito", "TheMovieDB")
                }
            }.decodeSingle<ApiKeys>();

            apiKeyTMDB = apiKeys.value;
        }

    }

    //functions that don't require the user to be logged in
    companion object {
        private val supabase = createSupabaseClient(
            supabaseUrl = "https://gxyzupxvwiuhyjtbbwmb.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd4eXp1cHh2d2l1aHlqdGJid21iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTYyOTkzMTMsImV4cCI6MjAzMTg3NTMxM30.4r45EIXsyGCFnsmyx9IcZPFF0NpxFuOrDvf4ghdgdEs"
        ) {
            install(Postgrest)
        }

        suspend fun getUser(userid: String): Users? {
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

        suspend fun getTMDBKey(): String {
            val columns = Columns.raw(ApiKeys.getStructure())
            val apiKeys = supabase.from("ApiKeys").select(columns = columns) {
                filter {
                    eq("sito", "TheMovieDB")
                }
            }.decodeSingle<ApiKeys>();

            return apiKeys.value;
        }

        suspend fun getRightVersionApp(): Int {
            return supabase.from("versionInfo").select().decodeSingle<VersionInfo>().version;
        }
    }

    /*--------------------------*/
    /*-----Custom functions-----*/
    /*--------------------------*/

    suspend fun getAllMediaDetails(): List<LocalMedia> {
        val media = supabase.postgrest.rpc("get_full_details_media") {
            filter {
                eq("userid", user.userId)
            }
        }.decodeList<AllDetailsMedia>()
        val results = mutableListOf<LocalMedia>();
        for (m in media) {
            val sameIdOnes = media.filter { it.mediaid == m.mediaid }
            val maybePiattaforme = sameIdOnes.map { Pair(it.nome, it.logo_path) }.filterNotNull()
            val localMedia = LocalMedia(
                m.mediaid,
                m.is_film,
                m.titolo,
                maybePiattaforme,
                m.poster_path,
                m.is_local,
                m.sinossi,
                m.annouscita,
                m.generi,
                m.durata,
                note = m.note
            )
            results.add(localMedia);
        }
        return results.toSet().toList();
    }

    private fun <T, U> List<Pair<T?, U?>>.filterNotNull() =
        mapNotNull { it.takeIf { it.first != null && it.second != null } as? Pair<T, U> }

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
            LiveDatas.addMedia(media)
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

    suspend fun deleteFromWatchList(mediaID: Int, adapterHomepage: AdapterHomepage) {
        supabase.from("watchlist").delete {
            filter {
                eq("mediaid", mediaID)
                eq("userid", user.userId)
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            LiveDatas.removeMedia(mediaID)
        }
    }

    suspend fun changeIsLocal(mediaId: Int, newState: Boolean) {
        supabase.from("watchlist").update({ set("is_local", newState) }) {
            filter {
                eq("userid", user.userId)
                eq("mediaid", mediaId)
            }
        }
    }

    suspend fun updateNote(mediaId: Int, newNote: String) {
        supabase.from("watchlist").update({ set("note", newNote) }) {
            filter {
                eq("userid", user.userId)
                eq("mediaid", mediaId)
            }
        }
        LiveDatas.updateNote(mediaId, newNote);
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
        val doveVedereMedia = supabase.from("DoveVedereMedia").select(columns = columns) {
            filter {
                eq("mediaID", mediaID)
            }
        }.decodeList<DoveVedereMedia>()
        val lastUpdates = doveVedereMedia.map { LocalDate.parse(it.lastUpdate) }
        for (lastUpdate in lastUpdates) {
            val today = LocalDate.now();
            val expirationDate = lastUpdate.plusDays(1);
            if (today.isAfter(expirationDate)) {
                val media = getMedia(mediaID)!!
                deleteDoveVedereMedia(mediaID);
                val mediaDetails = ViewModelProvider(appCompatActivity)[MediaDetails::class.java];
                val platforms = mediaDetails.getMediaPlatform(
                    mediaID,
                    media.is_film,
                    apiKeyTMDB
                )
                for (piattaforma in platforms) {
                    insertPiattaforma(piattaforma.first, mediaID);
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

    suspend fun getUser(): Users? {
        return supabase.from("Users").select(columns = Columns.raw(Users.getStructure())) {
            filter {
                eq("userId", user.userId)
            }
        }.decodeSingleOrNull<Users>()
    }

    suspend fun updateUser(userid: String, context: Context): Boolean {
        if (getUser(userid) != null) {
            return false;
        }

        supabase.from("Users").update({ set("userId", userid) }) {
            filter {
                eq("userId", user.userId)
            }
        }
        val userDao = Room.databaseBuilder(
            context,
            UserDatabase::class.java, "user-db"
        ).fallbackToDestructiveMigration()
            .build().userDao()

        user = getUser(userid)!!;
        userDao.deleteEvertyhing();
        userDao.insertUser(userid);
        return true;
    }

    suspend fun updateColor(color: ColoriName) {
        supabase.from("Users").update({ set("coloreTemaPrincipale", color.toString()) }) {
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
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val alreadyPresent = supabase.from("CronologiaMedia")
            .select(columns = Columns.raw(CronologiaMedia.getStructure())) {
                filter {
                    eq("userid", user.userId)
                    eq("mediaId", mediaId)
                    eq("dataVisione", today)
                }
            }.decodeSingleOrNull<CronologiaMedia>() != null;
        if (!alreadyPresent) {
            supabase.from("CronologiaMedia")
                .insert(InsertCronologiaMediaParams(user.userId, mediaId))
        }
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


}





