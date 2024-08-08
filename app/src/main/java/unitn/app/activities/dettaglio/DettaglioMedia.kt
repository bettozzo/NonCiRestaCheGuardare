package unitn.app.activities.dettaglio

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.test.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.remotedb.Colori
import unitn.app.remotedb.RemoteDAO


class DettaglioMedia : AppCompatActivity() {

    private var currentTab = 1;

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dettaglio_media)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //extras from ../homepage/AdapterHomepage.kt
        val extras = intent.extras;
        if (extras == null) {
            System.err.println("Bundle is null")
            return;
        }

        //id
        val id = extras.getInt("id");

        //titolo
        val titoloFilm = findViewById<TextView>(R.id.titoloFilm);
        setTitleProperties(titoloFilm, extras)

        //buttons
        val buttonDel = findViewById<ImageButton>(R.id.buttonDelete)
        val buttonSeen = findViewById<Button>(R.id.buttonSeen)
        setButtonProperties(buttonDel, buttonSeen, id)


        val isFilm = extras.getBoolean("isFilm");
        val viewFragAdapter = ViewPagerFragmentAdapter(this, extras);
        val tabLayout = findViewById<TabLayout>(R.id.pageSelection);
        val viewPager = findViewById<ViewPager2>(R.id.pager)
        viewPager.adapter = viewFragAdapter;
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (isFilm) {
                when (position) {
                    0 -> tab.text = "INFO";
                    1 -> tab.text = "STREAMING";
                    2 -> tab.text = "NOTE";
                }
            } else {
                //TODO scommenttare quando si aggiunge tab progresso
//                when (position) {
//                    0 -> tab.text = "INFO";
//                    1 -> tab.text = "PROGRESSO";
//                    2 -> tab.text = "STREAMING";
//                    3 -> tab.text = "NOTE";
//                }
                when (position) {
                    0 -> tab.text = "INFO";
                    1 -> tab.text = "STREAMING";
                    2 -> tab.text = "NOTE";
                }
            }
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTab = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        //use correct colors
        LiveDatas.updateColorsOfButtons(listOf(buttonSeen))
        val color = LiveDatas.getColore();
        tabLayout.setBackgroundColor(Color.parseColor(color.colorCode))
        val colore = Colori.getTabColore(color.colorName);
        tabLayout.setSelectedTabIndicatorColor(colore)
    }

    private fun setButtonProperties(
        deleteBtn: ImageButton,
        seenBtn: Button,
        mediaID: Int,
    ) {
        deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this@DettaglioMedia)
            with(builder) {
                setTitle("Sei sicuro?")
                setMessage("Sicuro di voler rimuovere questo titolo dalla lista?")
                setPositiveButton(android.R.string.ok) { _, _ ->
                    LiveDatas.setIdToRemove(mediaID);
                    finish()
                }
                setNegativeButton(android.R.string.cancel, null)
                show()
            }
        }

        seenBtn.setOnClickListener {
            lifecycleScope.launch {
                val remoteDao = RemoteDAO(
                    applicationContext,
                    coroutineContext
                );
                remoteDao.insertToCronologia(mediaID)
                LiveDatas.setIdToRemove(mediaID);
                finish();
            }
        }
    }

    private fun setTitleProperties(titoloFilm: TextView, extras: Bundle) {
        titoloFilm.text = extras.getString("titoloFilm")
        titoloFilm.ellipsize = TextUtils.TruncateAt.MARQUEE;
        titoloFilm.marqueeRepeatLimit = -1;
        titoloFilm.setSingleLine(true);
        titoloFilm.setSelected(true);
    }

}






