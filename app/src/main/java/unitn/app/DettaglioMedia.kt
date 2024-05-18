package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
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
import androidx.room.Room
import com.example.test.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import unitn.app.localdb.Converters
import unitn.app.localdb.MediaDatabase


class DettaglioMedia : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
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
        Picasso.get().load(extras.getString("poster")).placeholder(R.drawable.missing_poster)
            .into(findViewById<ImageView>(R.id.poster))

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
            val mediaDao = Room.databaseBuilder(
                applicationContext,
                MediaDatabase::class.java, "database-name"
            ).addTypeConverter(Converters())
                .build().MediaDao()

            lifecycleScope.launch {
                mediaDao.saveInLocal(id, isChecked)
            }
        }

        //buttons
        val buttonDel = findViewById<ImageButton>(R.id.buttonDelete)
        val buttonSeen = findViewById<ImageButton>(R.id.buttonSeen)
        setButtonProperties(buttonDel, buttonSeen, id)
    }

    private fun setButtonProperties(
        deleteBtn: ImageButton,
        seenBtn: ImageButton,
        movieId: Int,
    ) {
        deleteBtn.setOnClickListener {
            val intent = Intent(this@DettaglioMedia, HomePage::class.java)
            lifecycleScope.launch {
                val movieDao = Room.databaseBuilder(
                    applicationContext,
                    MediaDatabase::class.java, "database-name"
                ).addTypeConverter(Converters())
                    .build().MediaDao()
                movieDao.deleteMedia(movieId)
                finish()//prevents this activity to be opened again
                startActivity(intent)
            }
        }

        seenBtn.setOnClickListener {
            val intent = Intent(this@DettaglioMedia, HomePage::class.java)
            lifecycleScope.launch {
                val movieDao = Room.databaseBuilder(
                    applicationContext,
                    MediaDatabase::class.java, "database-name"
                ).addTypeConverter(Converters())
                    .build().MediaDao()
                movieDao.deleteMedia(movieId)
                finish()//prevents this activity to be opened again
                startActivity(intent)
            }
        }
    }

    private fun showAvailablePlatforms(extras: Bundle) {
        val platformList = findViewById<LinearLayout>(R.id.plaformList)
        val netflixLogo = extras.getString("NetflixPath")
        val primevideoLogo = extras.getString("AmazonPath")
        val disneyplusLogo = extras.getString("DisneyPath")


        if (netflixLogo == null && primevideoLogo == null && disneyplusLogo == null) {
            addPlatform(null, platformList)
        } else {
            if (netflixLogo != null) {
                addPlatform(netflixLogo, platformList)
            }
            if (primevideoLogo != null) {
                addPlatform(primevideoLogo, platformList)
            }
            if (disneyplusLogo != null) {
                addPlatform(disneyplusLogo, platformList)
            }
        }
    }

    private fun addPlatform(logoPath: String?, platformList: LinearLayout) {
        val platformView = ImageView(this);
        Picasso.get().load(logoPath).placeholder(R.drawable.no_providers).into(platformView);
        platformList.addView(platformView);
    }

    private fun setTitleProperties(titoloFilm: TextView, extras: Bundle) {
        titoloFilm.text = extras.getString("titoloFilm")
        titoloFilm.ellipsize = TextUtils.TruncateAt.MARQUEE;
        titoloFilm.marqueeRepeatLimit = -1;
        titoloFilm.setSingleLine(true);
        titoloFilm.setSelected(true);
    }
}



