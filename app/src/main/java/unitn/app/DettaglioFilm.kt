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
        val titotloFilm = findViewById<TextView>(R.id.titoloFilm);
        titotloFilm.text = extras.getString("titoloFilm")
        titotloFilm.ellipsize = TextUtils.TruncateAt.MARQUEE;
        titotloFilm.marqueeRepeatLimit = -1;

        //poster
        Picasso.get().load(extras.getString("poster")).placeholder(R.drawable.missing_poster)
            .into(findViewById<ImageView>(R.id.poster))

        //platforms
        val plaformList = findViewById<LinearLayout>(R.id.plaformList)
        val netflixLogo = extras.getString("NetflixPath")
        val primevideoLogo = extras.getString("AmazonPath")
        val disneyplusLogo = extras.getString("DisneyPath")

        if (netflixLogo != null) {
            val netflixView = ImageView(this);
            Picasso.get().load(netflixLogo).into(netflixView);
            plaformList.addView(netflixView);
        }
        if (primevideoLogo != null) {
            val primeView = ImageView(this);
            Picasso.get().load(primevideoLogo).into(primeView);
            plaformList.addView(primeView);
        }
        if (disneyplusLogo != null) {
            val disneyView = ImageView(this);
            Picasso.get().load(disneyplusLogo).into(disneyView);
            plaformList.addView(disneyView);
        }

        if (netflixLogo == null && primevideoLogo == null && disneyplusLogo == null) {
            val noProviders = ImageView(this);
            Picasso.get().load(R.drawable.no_providers).into(noProviders);
            plaformList.addView(noProviders);
        }
        // TODO switch
        //buttons
        val buttonDel = findViewById<ImageButton>(R.id.buttonDelete)
        val buttonSeen = findViewById<ImageButton>(R.id.buttonSeen)
        val buttonOk = findViewById<ImageButton>(R.id.buttonOk)

        buttonDel.setOnClickListener {
            val intent = Intent(this@DettaglioFilm, HomePage::class.java)
            lifecycleScope.launch {
                val movieDao = Room.databaseBuilder(
                    applicationContext,
                    MoviesDatabase::class.java, "database-name"
                ).addTypeConverter(Converters())
                    .build().movieDao()
                movieDao.deleteMovie(id)
                startActivity(intent)
            }
        }

        buttonSeen.setOnClickListener {
            val intent = Intent(this@DettaglioFilm, HomePage::class.java)
            lifecycleScope.launch {
                val movieDao = Room.databaseBuilder(
                    applicationContext,
                    MoviesDatabase::class.java, "database-name"
                ).addTypeConverter(Converters())
                    .build().movieDao()
                movieDao.deleteMovie(id)
                startActivity(intent)
            }
        }

        buttonOk.setOnClickListener {
            val intent = Intent(this@DettaglioFilm, HomePage::class.java)
            startActivity(intent)
        }
    }
}
