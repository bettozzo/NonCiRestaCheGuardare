package unitn.app.activities.aggiungiMedia

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import unitn.app.activities.LiveDatas
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

        //extras from: ../ricerca/AdapterRicerca.kt
        //extras from: ../profilo/FragmentCronologia.kt
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
        val annoUscita = extras.getString("annoUscita");

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
        //Anno Uscita
        val annoUscitaView = findViewById<TextView>(R.id.AnnoUscita)
        annoUscitaView.text = annoUscita;
        //poster
        if (poster != null) {
            Picasso.get().load(poster).into(findViewById<ImageView>(R.id.poster))

            findViewById<ImageView>(R.id.poster).setOnClickListener {
                val imageView = ImageView(this)
                Picasso.get().load(poster).into(imageView)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                val settingsDialog = Dialog(this)
                settingsDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
                settingsDialog.addContentView(
                    imageView, ViewGroup.LayoutParams(900, 1400)
                )
                settingsDialog.show()
            }
        } else {
            val posterView = findViewById<ImageView>(R.id.poster);
            posterView.layoutParams.height = 350;
            Picasso.get().load(R.drawable.missing_poster).into(posterView);
        }

        //sinossi
        val sinossiView = findViewById<TextView>(R.id.sinossiText);
        if (sinossi.isNotEmpty()) {
            sinossiView.text = sinossi
        } else {
            sinossiView.visibility = View.GONE;
            findViewById<TextView>(R.id.HeaderInfoMedia).visibility = View.GONE;
        }

        //credits
        //cast
        if (cast.isNotEmpty()) {
            val castView = findViewById<TextView>(R.id.castText)
            val builderCast = SpannableStringBuilder()
            for (c in cast) {
                builderCast.append("--- ").bold { append(c.first) }.append(" ---").append("\n")
                    .append(c.second).append("\n")
            }
            castView.text = builderCast
        } else {
            findViewById<TextView>(R.id.castCredits).visibility = View.GONE;
            findViewById<TextView>(R.id.castText).visibility = View.INVISIBLE;
        }
        //crew
        if (crew.isNotEmpty()) {
            val crewView = findViewById<TextView>(R.id.crewText)
            val builderCrew = SpannableStringBuilder()
            for (c in crew) {
                builderCrew.append("--- ").bold { append(c.first) }.append(" ---").append("\n")
                    .append(c.second).append("\n")

            }
            crewView.text = builderCrew
        } else {
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
                            sinossi
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