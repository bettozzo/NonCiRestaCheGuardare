package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.test.R
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import unitn.app.localdb.Converters
import unitn.app.localdb.MediaDatabase


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
        val movieDao = Room.databaseBuilder(
            applicationContext,
            MediaDatabase::class.java, "database-name"
        ).addTypeConverter(Converters())
            .fallbackToDestructiveMigration()
            .build().MediaDao()

        val goToSearchButton = findViewById<ImageButton>(R.id.goToSearchMediaButton)
        goToSearchButton.setOnClickListener {
            val intent = Intent(this@HomePage, Ricerca::class.java)
            startActivity(intent)
        }

        val gridView = findViewById<GridView>(R.id.GridView)
        val mediaSelected = findViewById<TabLayout>(R.id.MediaSelection);

        lifecycleScope.launch {
            val movies = movieDao.getAllMovies()
            gridView.adapter = AdapterHomepage(this@HomePage, movies)
        }
        mediaSelected.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0 != null) {
                    if (p0.text == resources.getString(R.string.media_selection_tab_text_films)) {
                        lifecycleScope.launch {
                            gridView.adapter =
                                AdapterHomepage(this@HomePage, movieDao.getAllMovies())
                        }
                    } else if (p0.text == resources.getString(R.string.media_selection_tab_text_series)) {
                        lifecycleScope.launch {
                            gridView.adapter =
                                AdapterHomepage(this@HomePage, movieDao.getAllSeries())
                        }
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })
    }

    override fun onResume() {
        super.onResume()

        val gridView = findViewById<GridView>(R.id.GridView)
        val mediaSelected = findViewById<TabLayout>(R.id.MediaSelection);
        val tabPosition = mediaSelected.selectedTabPosition;

        val movieDao = Room.databaseBuilder(
            applicationContext,
            MediaDatabase::class.java, "database-name"
        ).addTypeConverter(Converters())
            .fallbackToDestructiveMigration()
            .build().MediaDao()

        lifecycleScope.launch {
            if (tabPosition == 0) {
                gridView.adapter = AdapterHomepage(this@HomePage, movieDao.getAllMovies())
            } else {
                gridView.adapter = AdapterHomepage(this@HomePage, movieDao.getAllSeries())
            }
        }
        mediaSelected.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0 != null) {
                    if (p0.text == resources.getString(R.string.media_selection_tab_text_films)) {
                        lifecycleScope.launch {
                            gridView.adapter =
                                AdapterHomepage(this@HomePage, movieDao.getAllMovies())
                        }
                    } else if (p0.text == resources.getString(R.string.media_selection_tab_text_series)) {
                        lifecycleScope.launch {
                            gridView.adapter =
                                AdapterHomepage(this@HomePage, movieDao.getAllSeries())
                        }
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })
    }
}

