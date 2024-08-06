package unitn.app.activities.profilo

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.remotedb.RemoteDAO

class ProfiloStatistiche : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profilo_statistiche)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val testoPercentuale = findViewById<RelativeLayout>(R.id.percentualeVisione)
        //completismo
        var inPercentuale = true;
        setCompletamento(inPercentuale)
        testoPercentuale.setOnClickListener {
            inPercentuale = !inPercentuale
            setCompletamento(inPercentuale)
        }

    }
    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setCompletamento(
        inPercentuale: Boolean,
    ) {

        val headerPercentualeVisti = findViewById<TextView>(R.id.percentualeVisti)
        val vistiGenerico = findViewById<TextView>(R.id.percentualeVistiTotale)
        val dettaglioFilm = findViewById<TextView>(R.id.percentualeVistiFilmTotale)
        val dettaglioSerie = findViewById<TextView>(R.id.percentualeVistiSerieTVTotale)


        lifecycleScope.launch {
            val remoteDao = RemoteDAO(
                this@ProfiloStatistiche, coroutineContext
            );
            val cronologia = remoteDao.getCronologia()

            val filmVisti = cronologia.count { it.first.is_film }
            val serieViste = cronologia.count { !it.first.is_film }
            val filmDaVedere = LiveDatas.liveWatchlist.value?.count { it.isFilm } ?: 0
            val serieDaVedere = LiveDatas.liveWatchlist.value?.count { !it.isFilm } ?: 0

            val totaleFilm = filmVisti + filmDaVedere;
            val totaleSerie = serieViste + serieDaVedere;
            val totaleVisti = filmVisti + serieViste;
            val totaleDaVedere = filmDaVedere + serieDaVedere;
            val totale = totaleVisti + totaleDaVedere

            vistiGenerico.text = if (inPercentuale && totale != 0) {
                String.format("%.1f", (totaleVisti.toFloat() / totale.toFloat()) * 100) + "%"
            } else {
                "$totaleVisti/$totale"
            }

            dettaglioFilm.text = if (inPercentuale && totale != 0) {
                String.format("%.1f", (filmVisti.toFloat() / totale.toFloat()) * 100) + "%"
            } else {
                "$filmVisti/$totaleFilm"
            }
            dettaglioSerie.text = if (inPercentuale && totale != 0) {
                String.format("%.1f", (serieViste.toFloat() / totale.toFloat()) * 100) + "%"
            } else {
                "$serieViste/$totaleSerie"
            }

            headerPercentualeVisti.text = if (inPercentuale) {
                "Percentuale visti"
            } else {
                "Numero visti"
            }
        }
    }
}