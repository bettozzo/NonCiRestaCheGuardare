package unitn.app.activities.ratings

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.test.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.runBlocking
import unitn.app.activities.LiveDatas
import unitn.app.remotedb.Colori
import unitn.app.remotedb.CronologiaConRating
import unitn.app.remotedb.RemoteDAO
import unitn.app.remotedb.Users

class DettaglioMedia : AppCompatActivity() {


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rating_dettaglio_media)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //extras from ../profilo/ProfiloCronologia.kt
        val extras = intent.extras;
        if (extras == null) {
            System.err.println("Bundle is null")
            return;
        }

        val titolo = findViewById<TextView>(R.id.titoloFilm);
        val tabLayout = findViewById<TabLayout>(R.id.pageSelection);
        val viewPager = findViewById<ViewPager2>(R.id.pager);

        val cronologia: List<CronologiaConRating>;
        val ratingsMedia: List<Pair<Users, CronologiaConRating>>;
        runBlocking {
            cronologia = RemoteDAO(this@DettaglioMedia, coroutineContext).getCronologiaOfMedia(
                extras.getInt("mediaId")
            );
            ratingsMedia = RemoteDAO(this@DettaglioMedia, coroutineContext).getAllRatingsOfMedia(
                extras.getInt("mediaId")
            );
        }

        titolo.text = cronologia[0].media.titolo;
        val viewFragAdapter = ViewPagerFragmentAdapter(this, cronologia, ratingsMedia);
        viewPager.adapter = viewFragAdapter;
        viewPager.reduceDragSensitivity(1);


        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "La tua recensione";
            } else {
                tab.text = "Recensioni altrui";
            }
        }.attach()

        //use correct colors
        val color = LiveDatas.getColore();
        tabLayout.setBackgroundColor(color.colorCode.toColorInt())
        val colore = Colori.getTabColore(color.colorName);
        tabLayout.setSelectedTabIndicatorColor(colore)
    }

    private fun ViewPager2.reduceDragSensitivity(f: Int = 4) {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView

        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * f)       // "8" was obtained experimentally
    }
}
