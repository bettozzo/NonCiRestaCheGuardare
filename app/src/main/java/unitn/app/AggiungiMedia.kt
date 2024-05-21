package unitn.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.ImageView
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
import unitn.app.api.Media
import unitn.app.localdb.Converters
import unitn.app.localdb.MediaDatabase

class AggiungiMedia : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aggiungi_media)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //extras from: ./AdapterRicerca.kt
        val extras = intent.extras;
        if (extras == null) {
            System.err.println("Bundle is null")
            return;
        }

        val id = extras.getInt("id")
        val titolo = extras.getString("titoloMedia") ?: "ERRORE";
        val poster = extras.getString("poster");
        val isFilm = extras.getBoolean("isFilm");
        val platforms = Converters().stringToPlatform(extras.getString("platforms"));
        val sinossi = extras.getString("sinossi", "NO Sinossi");

        //titolo
        val titoloMedia = findViewById<TextView>(R.id.titoloFilm);
        setTitleProperties(titoloMedia, titolo)

        //poster
        if (poster != null) {
            Picasso.get().load(poster).into(findViewById<ImageView>(R.id.poster))
        }else{
            val posterView = findViewById<ImageView>(R.id.poster);
            posterView.layoutParams.height = 350;
            Picasso.get().load(R.drawable.missing_poster).into(posterView);
        }

        //sinossi
        val sinossiView = findViewById<TextView>(R.id.sinossiText);
        sinossiView.movementMethod = ScrollingMovementMethod();
        sinossiView.text = sinossi

        //bottone aggiungi
        val buttonAdd = findViewById<Button>(R.id.addFilm)
        buttonAdd.setOnClickListener {
            lifecycleScope.launch {
                val mediaDao = Room.databaseBuilder(
                    applicationContext,
                    MediaDatabase::class.java, "media-DB"
                ).addTypeConverter(Converters())
                    .build().MediaDao()
                mediaDao.insertMedia(Media(id, isFilm, titolo, platforms, poster, false, sinossi))
                finish()
            }
        }
    }

    private fun setTitleProperties(titoloFilm: TextView, titolo: String) {
        titoloFilm.text = titolo;
        titoloFilm.ellipsize = TextUtils.TruncateAt.MARQUEE;
        titoloFilm.marqueeRepeatLimit = -1;
        titoloFilm.setSingleLine(true);
        titoloFilm.setSelected(true);
    }
}