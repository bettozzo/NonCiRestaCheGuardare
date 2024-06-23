package unitn.app.activities.homepage

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.test.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.activities.profilo.Profilo
import unitn.app.activities.ricerca.Ricerca
import unitn.app.remotedb.RemoteDAO


class HomePage : AppCompatActivity() {

    private val viewFragAdapter = ViewPagerFragmentAdapter(this);
    private var currentTab = 0;

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        updateColor();

        LiveDatas.liveColore.observe(this, object : Observer<String> {
            override fun onChanged(value: String) {
                LiveDatas.updateColorsOfImgButtons(
                    listOf(
                        findViewById(R.id.goToProfile),
                        findViewById(R.id.goToSearchMediaButton)
                    )
                )
                val tab = findViewById<TabLayout>(R.id.pageSelection);
                tab.setBackgroundColor(Color.parseColor(value))
                when (value) {
                    "#2d95eb" -> {
                        //azzurro
                        tab.setSelectedTabIndicatorColor(Color.parseColor("#77edff"))
                    }
                    "#008c00" -> {
                        //verde
                        tab.setSelectedTabIndicatorColor(Color.parseColor("#A8FF2F"))}
                    "#852deb" -> {
                        //viola
                        tab.setSelectedTabIndicatorColor(Color.parseColor("#cf06f9"))}
                }
            }
        })

        val mediaSelected = findViewById<TabLayout>(R.id.pageSelection);
        val viewPager = findViewById<ViewPager2>(R.id.pager)
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

        mediaSelected.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTab = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onResume() {
        super.onResume()

        val viewPager = findViewById<ViewPager2>(R.id.pager)
        val goToSearchButton = findViewById<ImageButton>(R.id.goToSearchMediaButton)
        val goToProfileButton = findViewById<ImageButton>(R.id.goToProfile)

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
            LiveDatas.setColore(newColor.colorCode)
        }
    }
}


