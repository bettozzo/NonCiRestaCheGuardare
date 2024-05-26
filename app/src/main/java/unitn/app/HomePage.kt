package unitn.app

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.test.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import unitn.app.api.LocalMedia
import unitn.app.remotedb.RemoteDAO
import kotlin.coroutines.coroutineContext


class HomePage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        updateColor();

        LiveDatas.liveColore.observe(this, object: Observer<String>{
            override fun onChanged(value: String) {
                LiveDatas.updateColorsOfImgButtons(
                    listOf(
                        findViewById(R.id.goToProfile),
                        findViewById(R.id.goToSearchMediaButton)
                    )
                )
                findViewById<TabLayout>(R.id.MediaSelection).setBackgroundColor(Color.parseColor(value))
            }
        })

        LiveDatas.liveWatchlist.observe(this, object: Observer<List<LocalMedia>>{
            override fun onChanged(value: List<LocalMedia>) {
                findViewById<ViewPager2>(R.id.pager).adapter = ViewPagerFragmentAdapter(this@HomePage);
            }
        })

        if (LiveDatas.liveWatchlist.value!!.isEmpty()) {
            lifecycleScope.launch {
                syncDataDB()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val viewPager = findViewById<ViewPager2>(R.id.pager)
        val mediaSelected = findViewById<TabLayout>(R.id.MediaSelection);
        val viewFragAdapter = ViewPagerFragmentAdapter(this);
        val goToSearchButton = findViewById<ImageButton>(R.id.goToSearchMediaButton)
        val goToProfileButton = findViewById<ImageButton>(R.id.goToProfile)

        viewPager.adapter = viewFragAdapter;
        TabLayoutMediator(mediaSelected, viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "Films";
                tab.icon = ContextCompat.getDrawable(applicationContext, R.drawable.film_icon);
            } else {
                tab.text = "Serie TV";
                tab.icon = ContextCompat.getDrawable(applicationContext, R.drawable.series_icon);
            }
        }.attach()

        goToSearchButton.setOnClickListener {
            val intent = Intent(this@HomePage, Ricerca::class.java)
            startActivity(intent)
        }

        goToProfileButton.setOnClickListener {
            val intent = Intent(this@HomePage, Profilo::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

    }

    private fun updateColor() {
        lifecycleScope.launch {
            val newColor = RemoteDAO(
                applicationContext,
                coroutineContext
            ).getMainColor()
            LiveDatas.setColore(newColor.colorCode)
        }
    }

    private suspend fun syncDataDB() {
        val remoteDao = RemoteDAO(
            applicationContext,
            coroutineContext
        );

        //add remote to local
        val rMedias =
            remoteDao.getWatchList()
                .map { ConverterMedia.toLocal(applicationContext, it.first, it.second) }
        for (media in rMedias) {
            LiveDatas.addMedia(media)
        }
        LiveDatas.setIsDarkTheme(remoteDao.getDarkTheme())
        if (LiveDatas.liveIsDarkTheme.value!!) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}


