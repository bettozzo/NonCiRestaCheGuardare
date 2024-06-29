package unitn.app.activities.loadingScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import com.example.test.R
import kotlinx.coroutines.runBlocking
import unitn.app.activities.LiveDatas
import unitn.app.activities.homepage.HomePage
import unitn.app.remotedb.RemoteDAO
import kotlin.coroutines.coroutineContext

class LoadingScreen : AppCompatActivity() {
    private var isFinished: MutableLiveData<Boolean> = MutableLiveData(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        runBlocking {
            syncDataDB()
        }
        isFinished.observe(this) {
            if (it) {
                val intent = Intent(this@LoadingScreen, HomePage::class.java);
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("firstTimeLoading", true);
                startActivity(intent)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private suspend fun syncDataDB() {
        val remoteDao = RemoteDAO(
            applicationContext,
            coroutineContext
        );

        if (LiveDatas.liveIsDarkTheme.value!!) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        LiveDatas.setIsDarkTheme(remoteDao.getDarkTheme())


        //add remote to local
        val medias = remoteDao.getAllMediaDetails();

        if (medias.isEmpty()) {
            isFinished.value = true;
            return;
        }
        medias.map {
            LiveDatas.addMedia(it)
        }

        isFinished.value = true;
    }
}