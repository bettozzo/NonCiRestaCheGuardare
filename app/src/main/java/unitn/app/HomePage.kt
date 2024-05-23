package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.test.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import unitn.app.localdb.Converters
import unitn.app.localdb.MediaDatabase
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

//        lifecycleScope.launch {
//            val usersDao = Room.databaseBuilder(
//                applicationContext,
//                UserDatabase::class.java, "user-db"
//            ).addTypeConverter(Converters())
//                .fallbackToDestructiveMigration()
//                .build().userDao()
//            usersDao.inserUserId(Users("40e6215d-b5c6-4896-987c-f30f3678f608", CustomColors("Verde", "#008c00")));
//        }
    }

    override fun onResume() {
        super.onResume()

        val viewPager = findViewById<ViewPager2>(R.id.pager)
        val mediaSelected = findViewById<TabLayout>(R.id.MediaSelection);
        val viewFragAdapter = ViewPagerFragmentAdapter(this);
        val goToSearchButton = findViewById<ImageButton>(R.id.goToSearchMediaButton)

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

        lifecycleScope.launch {
            syncDataDB()
            changeColor();
        }
    }

    private fun changeColor() {
        val mediaSelected = findViewById<TabLayout>(R.id.MediaSelection);

        lifecycleScope.launch {
            mediaSelected.setBackgroundColor(
                RemoteDAO(
                    applicationContext,
                    coroutineContext
                ).getMainColor()
            );
        }
    }

    private suspend fun syncDataDB(){
        val localDao = Room.databaseBuilder(applicationContext, MediaDatabase::class.java, "media-DB")
            .addTypeConverter(Converters())
            .fallbackToDestructiveMigration()
            .build()
            .MediaDao();

        val remoteDao = RemoteDAO(
            applicationContext,
            coroutineContext
        );

        //todo se ha internet
        localDao.deleteEveryMedia();

        //add remote to local
        val rMedias = remoteDao.getWatchList().map { ConverterMedia.toLocal(applicationContext,it) }
        for(media in rMedias){
            localDao.insertMedia(media)
        }

        //todo remove sync?
    }
}


