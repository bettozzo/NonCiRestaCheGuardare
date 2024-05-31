package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import unitn.app.api.LocalMedia
import unitn.app.remotedb.RemoteDAO
import kotlin.coroutines.coroutineContext

class LoadingScreen : AppCompatActivity() {
    private var isFinished: MutableLiveData<Boolean> = MutableLiveData(false)
    private val orderAndId = mutableListOf<Pair<Int, Int>>()
    private val mediasGlobal = mutableListOf<LocalMedia>()
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
                orderAndId.sortedWith(compareBy { it.first }).map { (_, mediaId) ->
                    LiveDatas.addMedia(mediasGlobal.find { it.mediaId == mediaId }!!)}
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
            if (medias.isEmpty()) {
                isFinished.value = true;
                return;
            }

            val total = medias.size;
            val text = findViewById<TextView>(R.id.loadingText)
            var counter = 0;
            var order = 0;
            medias.map {
                order++;
                orderAndId.add(Pair(order, it.first.mediaID))
                lifecycleScope.launch(Dispatchers.IO) {
                    val media = ConverterMedia.toLocal(
                        applicationContext,
                        it.first,
                        it.second,
                        this@LoadingScreen
                    )
                    mediasGlobal.add(media)
                    counter++;
                    withContext(Dispatchers.Main) {
                        text.text = "$counter/$total"
                    }
                    if (counter == total) {
                        isFinished.postValue(true);
                    }
                }
            }
        }
    }