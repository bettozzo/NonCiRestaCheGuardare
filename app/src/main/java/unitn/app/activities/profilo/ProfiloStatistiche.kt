package unitn.app.activities.profilo

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.runBlocking
import unitn.app.activities.LiveDatas
import unitn.app.remotedb.RemoteDAO

class ProfiloStatistiche : AppCompatActivity() {


    private var filmVisti = 0;
    private var serieViste = 0;
    private var filmDaVedere = 0;
    private var serieDaVedere = 0;

    private var totaleFilm = 0;
    private var totaleSerie = 0;
    private var totaleVisti = 0;
    private var totaleDaVedere = 0;
    private var totale = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profilo_statistiche)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initDati();

        //completismo
        setCompletamento()

        //grafico a torta
        setPieCharInfo()
    }

    private fun initDati() {
        runBlocking {
            val remoteDao = RemoteDAO(
                this@ProfiloStatistiche, coroutineContext
            );
            val cronologia = remoteDao.getCronologia()
            filmVisti = cronologia.count { it.first.is_film }
            serieViste = cronologia.count { !it.first.is_film }
            filmDaVedere = LiveDatas.liveWatchlist.value?.count { it.isFilm } ?: 0
            serieDaVedere = LiveDatas.liveWatchlist.value?.count { !it.isFilm } ?: 0

            totaleFilm = filmVisti + filmDaVedere;
            totaleSerie = serieViste + serieDaVedere;
            totaleVisti = filmVisti + serieViste;
            totaleDaVedere = filmDaVedere + serieDaVedere;
            totale = totaleVisti + totaleDaVedere
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setCompletamento(
    ) {

        val headerPercentualeVisti = findViewById<TextView>(R.id.percentualeVisti)
        val vistiGenerico = findViewById<TextView>(R.id.percentualeVistiTotale)
        val dettaglioFilm = findViewById<TextView>(R.id.percentualeVistiFilmTotale)
        val dettaglioSerie = findViewById<TextView>(R.id.percentualeVistiSerieTVTotale)

        vistiGenerico.text = "$totaleVisti/$totale"
        dettaglioFilm.text = "$filmVisti/$totaleFilm"
        dettaglioSerie.text = "$serieViste/$totaleSerie"
        headerPercentualeVisti.text = "Numero visti"
    }

    private fun setPieCharInfo() {
        val pieChart = findViewById<PieChart>(R.id.pieChart)

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false


        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)


        pieChart.isDrawHoleEnabled = true
        if(LiveDatas.liveIsDarkTheme.value!!) {
            pieChart.setHoleColor(Color.DKGRAY)
        }else{
            pieChart.setHoleColor(Color.WHITE)
        }
        pieChart.holeRadius = 50f
        pieChart.transparentCircleRadius = 55f
        pieChart.setDrawCenterText(true)
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)


        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(filmVisti.toFloat(), "Film"))
        entries.add(PieEntry(serieViste.toFloat(), "Serie"))
        entries.add(PieEntry(totaleDaVedere.toFloat(), "Da vedere"))
        val dataSet = PieDataSet(entries, "Film vs Serie viste")

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)


        dataSet.sliceSpace = 3f

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.Azzurro))
        colors.add(resources.getColor(R.color.Verde))
        colors.add(resources.getColor(R.color.GrigioScuro))
        dataSet.colors = colors

        // loading chart
        pieChart.highlightValues(null)
        pieChart.invalidate()
    }
}