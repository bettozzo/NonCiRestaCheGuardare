package unitn.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
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
        setContentView(R.layout.activity_aggiungi_film)
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
        val titolo = extras.getString("titoloFilm") ?: "ERRORE";
        val poster = extras.getString("poster");
        val isFilm = extras.getBoolean("isFilm");
        val platforms = Converters().stringToPlatform(extras.getString("platforms"));


        val titotloFilm = findViewById<TextView>(R.id.titoloFilm);
        setTitleProperties(titotloFilm, titolo)

        Picasso.get().load(poster).placeholder(R.drawable.missing_poster)
            .into(findViewById<ImageView>(R.id.poster))


        val buttonAdd = findViewById<Button>(R.id.addFilm)
        buttonAdd.setOnClickListener {
            lifecycleScope.launch {
                val movieDao = Room.databaseBuilder(
                    applicationContext,
                    MediaDatabase::class.java, "database-name"
                ).addTypeConverter(Converters())
                    .build().MediaDao()
                val posterNN = poster ?: "no poster"
                movieDao.insertMedia(Media(id, isFilm, titolo, platforms, posterNN, false))
                setResult(id)
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