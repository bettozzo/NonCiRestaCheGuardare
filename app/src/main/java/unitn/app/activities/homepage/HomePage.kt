package unitn.app.activities.homepage

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.test.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.activities.feed.Feed
import unitn.app.activities.profilo.Profilo
import unitn.app.activities.ricerca.Ricerca
import unitn.app.remotedb.Colori
import unitn.app.remotedb.RemoteDAO


class HomePage : AppCompatActivity() {

    private val viewFragAdapter = ViewPagerFragmentAdapter(this);

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            insets;
        }
        val extras = intent.extras;
        if (extras == null) {
            System.err.println("Bundle is null");
            return;
        }

        val mediaSelected = findViewById<TabLayout>(R.id.pageSelection);
        val viewPager = findViewById<ViewPager2>(R.id.pager);
        viewPager.reduceDragSensitivity(1);
        viewPager.adapter = viewFragAdapter;

        val feedButton = findViewById<TextView>(R.id.feed);
        feedButton.setOnClickListener {
            val intent = Intent(this, Feed::class.java);
            startActivity(intent);
        }


        TabLayoutMediator(mediaSelected, viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "Films";
                tab.icon = ContextCompat.getDrawable(applicationContext, R.drawable.film_icon);
            } else {
                tab.text = "Serie TV";
                tab.icon = ContextCompat.getDrawable(applicationContext, R.drawable.series_icon);
            }
        }.attach()


        val firstTimeLoading = extras.getBoolean("firstTimeLoading", false);
        if (firstTimeLoading) {
            val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putInt("currentTab", 0)
                apply()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        updateColor();
        val viewPager = findViewById<ViewPager2>(R.id.pager)
        val goToSearchButton = findViewById<ImageButton>(R.id.goToSearchMediaButton)
        val goToProfileButton = findViewById<TextView>(R.id.opzioni)
        val mediaSelected = findViewById<TabLayout>(R.id.pageSelection);
        val feedButton = findViewById<TextView>(R.id.feed);
        feedButton.setOnClickListener {
            val intent = Intent(this, Feed::class.java);
            startActivity(intent);
        }


        mediaSelected.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
                with(sharedPref.edit()) {
                    putInt("currentTab", tab.position)
                    apply()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val currentTab = sharedPref.getInt("currentTab", 0);
        viewPager.currentItem = currentTab;

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
            LiveDatas.setColore(newColor.colorName)
        }
        LiveDatas.updateColorsOfImgButtons(listOf(findViewById(R.id.goToSearchMediaButton)))

        val color = LiveDatas.getColore()
        val tab = findViewById<TabLayout>(R.id.pageSelection);
        tab.setBackgroundColor(color.colorCode.toColorInt())
        val coloreTab = Colori.getTabColore(color.colorName);
        tab.setSelectedTabIndicatorColor(coloreTab)


        val menu = findViewById<LinearLayout>(R.id.quickMenu);
        menu.setBackgroundColor(color.colorCode.toColorInt());
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
