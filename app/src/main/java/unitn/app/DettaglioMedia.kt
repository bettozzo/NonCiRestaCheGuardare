package unitn.app

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unitn.app.remotedb.RemoteDAO


class DettaglioMedia : AppCompatActivity() {
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

        //extras from AdapterHomepage.kt
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

        //poster
        val poster = findViewById<ImageView>(R.id.poster);
        val posterPath = extras.getString("poster");
        Picasso.get().load(posterPath).placeholder(R.drawable.missing_poster)
            .into(poster)

        if (posterPath != null) {
            poster.setOnClickListener {
                val imageView = ImageView(this)
                Picasso.get().load(posterPath).placeholder(R.drawable.missing_poster)
                    .into(imageView)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                val settingsDialog = Dialog(this)
                settingsDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
                settingsDialog.addContentView(
                    imageView, ViewGroup.LayoutParams(900, 1500)
                )
                settingsDialog.show()
            }
        }

        //sinossi
        val sinossiView = findViewById<TextView>(R.id.sinossiText)
        val sinossi = extras.getString("sinossi", "no sinossi");
        sinossiView.text = sinossi;
        sinossiView.movementMethod = ScrollingMovementMethod();

        //platforms
        showAvailablePlatforms(extras)

        //switch
        val switch = findViewById<SwitchCompat>(R.id.switchLocal)
        switch.isChecked = extras.getBoolean("isInLocal")
        switch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                val remoteDao = RemoteDAO(
                    applicationContext,
                    coroutineContext
                );
                remoteDao.changeIsLocal(id, isChecked)
            }
        }

        //buttons
        val buttonDel = findViewById<ImageButton>(R.id.buttonDelete)
        val buttonSeen = findViewById<ImageButton>(R.id.buttonSeen)
        setButtonProperties(buttonDel, buttonSeen, id)
        LiveDatas.liveColore.observe(this) {
            LiveDatas.updateColorsOfImgButtons(listOf(buttonSeen))
        }
    }

    private fun setButtonProperties(
        deleteBtn: ImageButton,
        seenBtn: ImageButton,
        mediaID: Int,
    ) {
        deleteBtn.setOnClickListener {
            val intent = Intent(this@DettaglioMedia, HomePage::class.java)
            lifecycleScope.launch {
                val remoteDao = RemoteDAO(
                    applicationContext,
                    coroutineContext
                );
                remoteDao.deleteFromWatchList(mediaID)
                LiveDatas.removeMedia(mediaID)
                finish()//prevents this activity to be opened again
                startActivity(intent)
            }
        }

        seenBtn.setOnClickListener {
            val intent = Intent(this@DettaglioMedia, HomePage::class.java)
            lifecycleScope.launch {
                val remoteDao = RemoteDAO(
                    applicationContext,
                    coroutineContext
                );

                remoteDao.insertToCronologia(mediaID)
                remoteDao.deleteFromWatchList(mediaID)
                LiveDatas.removeMedia(mediaID)
                finish()//prevents this activity to be opened again
                startActivity(intent)
            }
        }
    }

    private fun showAvailablePlatforms(extras: Bundle) = runBlocking {

        var hasAtLeastOnePlatofrm = false;
        val platforms = RemoteDAO(applicationContext, coroutineContext).getPiattaformeUser()

        val platformList = findViewById<LinearLayout>(R.id.plaformList)

        val netflix = platforms.find { it.nome == "Netflix" }
        val primevideoLogo = platforms.find { it.nome == "Amazon Prime Video" }
        val disneyplusLogo = platforms.find { it.nome == "Disney Plus" }
        val raiplayLogo = platforms.find { it.nome == "Rai Play" }
        val crunchyrollLogo = platforms.find { it.nome == "Crunchyroll" }

        if (netflix != null && extras.getString("NetflixPath") != null) {
            addPlatform(netflix.logo_path, platformList, this@DettaglioMedia)
            hasAtLeastOnePlatofrm = true;
        }

        if (primevideoLogo != null && extras.getString("AmazonPath") != null) {
            addPlatform(primevideoLogo.logo_path, platformList, this@DettaglioMedia)
            hasAtLeastOnePlatofrm = true;
        }
        if (disneyplusLogo != null && extras.getString("DisneyPath") != null) {
            addPlatform(disneyplusLogo.logo_path, platformList, this@DettaglioMedia)
            hasAtLeastOnePlatofrm = true;
        }
        if (raiplayLogo != null && extras.getString("RaiPath") != null) {
            addPlatform(raiplayLogo.logo_path, platformList, this@DettaglioMedia)
            hasAtLeastOnePlatofrm = true;
        }
        if (crunchyrollLogo != null && extras.getString("CrunchyrollPath") != null) {
            addPlatform(crunchyrollLogo.logo_path, platformList, this@DettaglioMedia)
            hasAtLeastOnePlatofrm = true;
        }

        if (!hasAtLeastOnePlatofrm) {
            val platformView = ImageView(this@DettaglioMedia);
            when (this@DettaglioMedia.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    Picasso.get().load(R.drawable.theme_dark_no_providers).into(platformView);
                }

                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    Picasso.get().load(R.drawable.theme_light_no_providers).into(platformView);
                }
            }
            platformList.addView(platformView);
        }
    }
}

private fun addPlatform(
    logoPath: String?,
    platformList: LinearLayout,
    applicationContext: Context,
) {
    val platformView = ImageView(applicationContext);
    Picasso.get().load(logoPath).placeholder(R.drawable.theme_light_no_providers)
        .into(platformView);
    platformList.addView(platformView);
}

private fun setTitleProperties(titoloFilm: TextView, extras: Bundle) {
    titoloFilm.text = extras.getString("titoloFilm")
    titoloFilm.ellipsize = TextUtils.TruncateAt.MARQUEE;
    titoloFilm.marqueeRepeatLimit = -1;
    titoloFilm.setSingleLine(true);
    titoloFilm.setSelected(true);
}




