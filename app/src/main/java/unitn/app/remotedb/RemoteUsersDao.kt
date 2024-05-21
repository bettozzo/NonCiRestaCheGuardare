package unitn.app.remotedb

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.room.Room
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.runBlocking
import unitn.app.localdb.UserDatabase

class RemoteUsersDao(mContext: Context){
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://gxyzupxvwiuhyjtbbwmb.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd4eXp1cHh2d2l1aHlqdGJid21iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTYyOTkzMTMsImV4cCI6MjAzMTg3NTMxM30.4r45EIXsyGCFnsmyx9IcZPFF0NpxFuOrDvf4ghdgdEs"
    ) {
        install(Postgrest)
    }
    val context = mContext;
    private var userId: String? = null;

    private suspend fun getCurrentUser(): Users {
        if (userId == null) {
            val userDao = Room.databaseBuilder(
                context,
                UserDatabase::class.java, "user-db"
                ).addTypeConverter(Converters())
                .build().userDao()
            userId = userDao.getUserId();

        }

        val columns = Columns.raw(
            """
                userId,
                coloreTemaPrincipale (
                  colorName,
                  colorCode
                )
            """.trimIndent()
        )
        val result = supabase.from("Users").select(columns = columns) {
            filter {
                eq("userId", userId!!);
            }
        }.decodeSingle<Users>()
        return result;

    }

    suspend fun getMainColor(): Int {
        val user = getCurrentUser()
        return Color.parseColor(user.coloreTemaPrincipale.colorCode)
    }
}

