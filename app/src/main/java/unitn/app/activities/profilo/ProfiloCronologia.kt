package unitn.app.activities.profilo

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.runBlocking
import unitn.app.activities.LiveDatas
import unitn.app.activities.ratings.DettaglioMedia
import unitn.app.remotedb.CronologiaConRating
import unitn.app.remotedb.Media
import unitn.app.remotedb.RemoteDAO


class ProfiloCronologia : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        enableEdgeToEdge()
        setContentView(R.layout.activity_profilo_cronologia)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cronologia: List<CronologiaConRating>;
        runBlocking {
            cronologia = RemoteDAO(this@ProfiloCronologia, coroutineContext).getCronologia()
        }
        Thread {
            populateCronologia(cronologia);
        }.start();
    }

    private fun populateCronologia(cronologia: List<CronologiaConRating>) {
        val lista = findViewById<LinearLayout>(R.id.cronologiaLista)
        val inflater = LayoutInflater.from(this@ProfiloCronologia)
        var lastDateSeen = "";
        for ((media, date, rating, maxRating, recensione) in cronologia) {
            //linea separazione per mese)
            if (getMonthYear(date) != lastDateSeen) {
                lastDateSeen = getMonthYear(date);
                val separator =
                    inflater.inflate(R.layout.item_separatore_data, lista, false);
                separator.findViewById<TextView>(R.id.data).text = lastDateSeen.toLiteralDate();
                runOnUiThread {
                    lista.addView(separator);
                }
            }
            val view = createView(media, rating, maxRating);
            runOnUiThread {
                lista.addView(view);
            }
        }
    }

    private fun createView(
        media: Media,
        rating: Float?,
        maxRating: Float?,
    ): View {
        val lista = findViewById<LinearLayout>(R.id.cronologiaLista)
        val inflater = LayoutInflater.from(this@ProfiloCronologia)


        //entry della cronologia
        val viewItem = inflater.inflate(R.layout.item_cronologia, lista, false);
        val poster = viewItem.findViewById<ImageView>(R.id.poster);
        val titoloView = viewItem.findViewById<TextView>(R.id.titoloFilm)
        val ratingBar = viewItem.findViewById<RatingBar>(R.id.rating);

        runOnUiThread {
            Picasso.get().load(media.poster_path).placeholder(R.drawable.missing_poster)
                .into(poster);
        }
        titoloView.text = media.titolo;

        if (maxRating != null && rating != null) {
            ratingBar.rating = rating.toFloat();
            ratingBar.numStars = maxRating.toInt();
        } else {
            val nonRecensitoView = viewItem.findViewById<Button>(R.id.nonRecensito);
            nonRecensitoView.visibility = View.VISIBLE;
            ratingBar.visibility = View.GONE;
            LiveDatas.updateColorsOfButtons(listOf(nonRecensitoView))
        }

        viewItem.setOnClickListener {
            val intent = Intent(this@ProfiloCronologia, DettaglioMedia::class.java)
            intent.putExtra("mediaId", media.mediaID);
            intent.putExtra("titoloMedia", media.titolo);
            startActivity(intent)
        }
        return viewItem;
    }

    private fun getMonthYear(dateYMD: String): String {
        val firstDate = dateYMD.replace("/", "-").split("-");
        return firstDate[0] + "/" + firstDate[1];
    }


    private fun String.toLiteralDate(): String {
        val months = mapOf(
            "01" to "Gennaio", "02" to "Febbraio", "03" to "Marzo",
            "04" to "Aprile", "05" to "Maggio", "06" to "Giugno",
            "07" to "Luglio", "08" to "Agosto", "09" to "Settembre",
            "10" to "Ottobre", "11" to "Novembre", "12" to "Dicembre"
        )

        val parts = this.replace("/", "-").split("-");
        val month: String;
        val year: String;

        if (parts[1].length == 2) {
            month = months[parts[1]].toString()
            year = parts[0]
        } else {
            month = months[parts[0]].toString()
            year = parts[1]
        }

        return "$month $year"
    }
}
