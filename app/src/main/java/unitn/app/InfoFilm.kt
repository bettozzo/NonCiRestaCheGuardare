package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
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
import unitn.app.api.Movies
import unitn.app.localdb.Converters
import unitn.app.localdb.MoviesDatabase

class InfoFilm : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info_film)
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

        val id = extras.getInt("id")
        val titolo = extras.getString("titoloFilm") ?: "ERRORE";
        val poster = extras.getString("poster");


        val titotloFilm = findViewById<TextView>(R.id.titoloFilm);
        titotloFilm.text = titolo
        titotloFilm.ellipsize = TextUtils.TruncateAt.MARQUEE;
        titotloFilm.marqueeRepeatLimit = -1;

        Picasso.get().load(poster).placeholder(R.drawable.missing_poster)
            .into(findViewById<ImageView>(R.id.poster))


        val buttonAdd = findViewById<Button>(R.id.addFilm)
        buttonAdd.setOnClickListener {
            val intent = Intent(this@InfoFilm, Search::class.java)
            lifecycleScope.launch {
                val movieDao = Room.databaseBuilder(
                    applicationContext,
                    MoviesDatabase::class.java, "database-name"
                ).addTypeConverter(Converters())
                    .build().movieDao()
                val posterNN = poster ?: "no poster"
                movieDao.insertMovie(Movies(id, titolo, emptyList(), posterNN))
                startActivity(intent)
            }
        }
    }
}