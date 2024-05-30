package unitn.app

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import unitn.app.api.LocalMedia
import unitn.app.localdb.Converters
import unitn.app.remotedb.RemoteDAO

class AggiungiMedia : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        val cast = Converters().stringToPlatform(extras.getString("cast"));
        val crew = Converters().stringToPlatform(extras.getString("crew"));

        //titolo
        val titoloMedia = findViewById<TextView>(R.id.titoloFilm);
        setTitleProperties(titoloMedia, titolo)

        //isFilm
        val isFilmview = findViewById<TextView>(R.id.isFilm)
        isFilmview.text = if (isFilm) {
            "Film"
        } else {
            "Serie TV"
        }
        //poster
        if (poster != null) {
            Picasso.get().load(poster).into(findViewById<ImageView>(R.id.poster))
        } else {
            val posterView = findViewById<ImageView>(R.id.poster);
            posterView.layoutParams.height = 350;
            Picasso.get().load(R.drawable.missing_poster).into(posterView);
        }

        //sinossi
        val sinossiView = findViewById<TextView>(R.id.sinossiText);
        sinossiView.movementMethod = ScrollingMovementMethod();
        sinossiView.text = sinossi

        //credits
        //cast
        val castView = findViewById<TextView>(R.id.castText)
        val builderCast = SpannableStringBuilder()
        for (c in cast) {
            builderCast.append("--- ").bold { append(c.first) }.append(" ---").append("\n")
                .append(c.second).append("\n")
        }
        castView.text = builderCast
        //crew
        if(crew.isNotEmpty()) {
            val crewView = findViewById<TextView>(R.id.crewText)
            val builderCrew = SpannableStringBuilder()
            for (c in crew) {
                builderCrew.append("--- ").bold { append(c.first) }.append(" ---").append("\n")
                    .append(c.second).append("\n")

            }
            crewView.text = builderCrew
        }else{
            findViewById<TextView>(R.id.crewCredits).visibility = View.GONE;
            findViewById<TextView>(R.id.crewText).visibility = View.INVISIBLE;
        }

        //bottone aggiungi
        val buttonAdd = findViewById<Button>(R.id.addMedia)
        buttonAdd.setOnClickListener {
            lifecycleScope.launch {
                if (LiveDatas.liveWatchlist.value?.filter { it.mediaId == id }!!.isEmpty()) {
                    val remoteDao = RemoteDAO(
                        applicationContext,
                        coroutineContext
                    );
                    val localMedia =
                        LocalMedia(
                            id,
                            isFilm,
                            titolo,
                            platforms,
                            poster,
                            false,
                            sinossi,
                            emptyList(),
                            emptyList()
                        );
                    remoteDao.insertToWatchlist(localMedia, this@AggiungiMedia)
                    LiveDatas.addMedia(localMedia)
                    LiveDatas.removeRicercaMedia(localMedia)
                } else {
                    Toast.makeText(applicationContext, "Media già presente", Toast.LENGTH_SHORT)
                        .show()
                }
                finish()
            }
        }

        LiveDatas.liveColore.observe(this) {
            LiveDatas.updateColorsOfButtons(listOf(buttonAdd))
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