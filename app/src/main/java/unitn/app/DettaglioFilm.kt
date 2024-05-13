package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.test.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import unitn.app.localdb.Converters
import unitn.app.localdb.MoviesDatabase

class DettaglioFilm : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dettaglio_film)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

        //platforms
        showAvailablePlatforms(extras)

        // TODO switch

        //buttons
        val buttonDel = findViewById<ImageButton>(R.id.buttonDelete)
        val buttonSeen = findViewById<ImageButton>(R.id.buttonSeen)
        setButtonProperties(buttonDel, buttonSeen, id)
    }

    private fun setButtonProperties(
        deleteBtn: ImageButton,
        seenBtn: ImageButton,
        movieId: Int
    ) {
        deleteBtn.setOnClickListener {
            val intent = Intent(this@DettaglioFilm, HomePage::class.java)
            lifecycleScope.launch {
                val movieDao = Room.databaseBuilder(
                    applicationContext,
                    MoviesDatabase::class.java, "database-name"
                ).addTypeConverter(Converters())
                    .build().movieDao()
                movieDao.deleteMovie(movieId)
                startActivity(intent)
            }
        }

        seenBtn.setOnClickListener {
            val intent = Intent(this@DettaglioFilm, HomePage::class.java)
            lifecycleScope.launch {
                val movieDao = Room.databaseBuilder(
                    applicationContext,
                    MoviesDatabase::class.java, "database-name"
                ).addTypeConverter(Converters())
                    .build().movieDao()
                movieDao.deleteMovie(movieId)
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

