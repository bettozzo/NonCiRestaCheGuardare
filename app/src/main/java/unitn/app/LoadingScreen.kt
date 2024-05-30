package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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
        Log.d("custom debug", "start")
        runBlocking {
            syncDataDB()
        }
        isFinished.observe(this) {
            if (it) {
                Log.d("custom debug", "finished")
                startActivity(Intent(this@LoadingScreen, HomePage::class.java))
                finish()
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
        val medias = remoteDao.getWatchList();
        val total = medias.size;
        val text = findViewById<TextView>(R.id.loadingText)
        var counter = 0;
        medias.map {
            lifecycleScope.launch(Dispatchers.IO) {
                val media = ConverterMedia.toLocal(
                    applicationContext,
                    it.first,
                    it.second
                )
                counter++;
                withContext(Dispatchers.Main) {
                    text.text = "$counter/$total"
                }
                LiveDatas.addMedia(media)
                Log.d("custom debug", "$media")
                if (counter == total) {
                    isFinished.postValue(true);
                }
            }
        }
    }
}