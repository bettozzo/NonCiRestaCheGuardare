package unitn.app.activities.customMedia

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.api.LocalMedia
import unitn.app.remotedb.RemoteDAO

class AggiungiCustomMedia : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aggiungi_custom_media)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val titoloView = findViewById<EditText>(R.id.titolo)
        val posterView = findViewById<EditText>(R.id.poster)
        val sinossiView = findViewById<EditText>(R.id.sinossi)
        val filmView = findViewById<TextView>(R.id.filmView)
        val serieView = findViewById<TextView>(R.id.serieTvView)
        val addMedia = findViewById<Button>(R.id.addMedia)

        val colore = LiveDatas.getColore();
        LiveDatas.updateColorsOfButtons(listOf(findViewById(R.id.addMedia)))
        filmView.setBackgroundColor(Color.parseColor(colore.colorCode))
        filmView.background.alpha = (0.7 * 255).toInt();
        serieView.setBackgroundColor(Color.TRANSPARENT)

        var isFilm = true;
        filmView.setOnClickListener {
            filmView.setBackgroundColor(Color.parseColor(colore.colorCode))
            filmView.background.alpha = (0.7 * 255).toInt();
            serieView.setBackgroundColor(Color.TRANSPARENT)
            isFilm = true;
        }
        serieView.setOnClickListener {
            serieView.setBackgroundColor(Color.parseColor(colore.colorCode))
            serieView.background.alpha = (0.7 * 255).toInt();
            filmView.setBackgroundColor(Color.TRANSPARENT)
            isFilm = false;
        }

        addMedia.setOnClickListener {
            if (titoloView.text.isEmpty()) {
                val builder = AlertDialog.Builder(this@AggiungiCustomMedia)
                with(builder) {
                    setTitle("Titolo non valido")
                    setMessage("Il titolo deve essere presente.")
                    setPositiveButton(android.R.string.ok, null)
                    show()
                }
                return@setOnClickListener;
            }
            lifecycleScope.launch {
                val remoteDao = RemoteDAO(
                    applicationContext,
                    coroutineContext
                );
                val id = remoteDao.getRandomHighID().possibiliID;

                val poster =
                    if (posterView.text.isEmpty() || !posterView.text.toString().isValidUrl()) {
                        null
                    } else {
                        posterView.text.toString()
                    }

                val localMedia =
                    LocalMedia(
                        id,
                        isFilm,
                        titoloView.text.toString(),
                        emptyList(),
                        poster,
                        false,
                        sinossiView.text.toString()
                    );
                remoteDao.insertToWatchlist(localMedia, this@AggiungiCustomMedia)
                LiveDatas.addMedia(localMedia)
                LiveDatas.removeRicercaMedia(localMedia)
                finish()
            }
        }
    }
}

fun String.isValidUrl(): Boolean {
    return this.startsWith("http://") || this.startsWith("https://") && Patterns.WEB_URL.matcher(
        this
    ).matches()
}