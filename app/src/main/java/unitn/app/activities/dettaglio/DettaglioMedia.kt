package unitn.app.activities.dettaglio

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.test.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import unitn.app.activities.LiveDatas
import unitn.app.activities.segnaComeVisto.SegnaComeVisto
import unitn.app.remotedb.Colori


class DettaglioMedia : AppCompatActivity() {

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dettaglio_media)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //extras from ../homepage/AdapterHomepage.kt e ../feed/Feed.kt
        val extras = intent.extras;
        if (extras == null) {
            System.err.println("Bundle is null")
            return;
        }

        //id
        val id = extras.getInt("id");

        //titolo
        val titoloFilm = findViewById<TextView>(R.id.titoloFilm);
        setTitleProperties(titoloFilm, extras.getString("titoloFilm"))

        //buttons
        val buttonDel = findViewById<ImageButton>(R.id.buttonDelete)
        val buttonSeen = findViewById<Button>(R.id.buttonSeen)
        setButtonProperties(buttonDel, buttonSeen, id, extras.getString("titoloFilm"))


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

        //use correct colors
        LiveDatas.updateColorsOfButtons(listOf(buttonSeen))
        val color = LiveDatas.getColore();
        tabLayout.setBackgroundColor(color.colorCode.toColorInt())
        val colore = Colori.getTabColore(color.colorName);
        tabLayout.setSelectedTabIndicatorColor(colore)
    }

    private fun setButtonProperties(
        deleteBtn: ImageButton,
        seenBtn: Button,
        mediaID: Int,
        titoloFilm: String?,
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
            val intent = Intent(this, SegnaComeVisto::class.java)
            intent.putExtra("isNewRating", true);
            intent.putExtra("mediaID", mediaID);
            intent.putExtra("titoloFilm", titoloFilm);
            startActivity(intent);
            finish()
        }
    }

    private fun setTitleProperties(titoloFilm: TextView, titolo: String?) {
        titoloFilm.text = titolo;
        titoloFilm.ellipsize = TextUtils.TruncateAt.MARQUEE;
        titoloFilm.marqueeRepeatLimit = -1;
        titoloFilm.isSingleLine = true;
        titoloFilm.isSelected = true;
    }

}
