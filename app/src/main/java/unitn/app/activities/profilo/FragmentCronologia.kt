package unitn.app.activities.profilo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.remotedb.RemoteDAO

class FragmentCronologia : Fragment() {
    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_cronologia, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val testoPercentuale = view.findViewById<RelativeLayout>(R.id.completamentoLista)

        //completismo
        var inPercentuale = true;
        setCompletamento(inPercentuale, view.context, view)
        testoPercentuale.setOnClickListener {
            inPercentuale = !inPercentuale
            setCompletamento(inPercentuale, view.context, view)
        }

        populateCronologia(view);
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setCompletamento(
        inPercentuale: Boolean,
        context: Context,
        view: View,
    ) {

        val headerPercentualeVisti = view.findViewById<TextView>(R.id.percentualeVisti)
        val vistiGenerico = view.findViewById<TextView>(R.id.percentualeVistiTotale)
        val dettaglioFilm = view.findViewById<TextView>(R.id.percentualeVistiFilmTotale)
        val dettaglioSerie = view.findViewById<TextView>(R.id.percentualeVistiSerieTVTotale)


        lifecycleScope.launch {
            val remoteDao = RemoteDAO(
                context, coroutineContext
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

    private fun populateCronologia(view: View) {
        val lista = view.findViewById<LinearLayout>(R.id.cronologiaLista)
        val inflater = LayoutInflater.from(view.context)
        lifecycleScope.launch {
            val cronologia = RemoteDAO(view.context, coroutineContext).getCronologia()
            for ((media, data) in cronologia) {
                val viewItem = inflater.inflate(R.layout.item_cronologia, lista, false);
                Picasso.get().load(media.poster_path).placeholder(R.drawable.missing_poster)
                    .into(viewItem.findViewById<ImageView>(R.id.poster))
                viewItem.findViewById<TextView>(R.id.titoloFilm).text = media.titolo
                val dataYMD = data.split("-")
                val dataDMY = dataYMD[2] + "/" + dataYMD[1] + "/" + dataYMD[0]
                viewItem.findViewById<TextView>(R.id.data).text = dataDMY
                lista.addView(viewItem)
            }
        }
    }
}