package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import unitn.app.localdb.Converters
import unitn.app.remotedb.RemoteDAO


class CronologiaMedia : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cronologia_media)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val extras = intent.extras!!
        findViewById<TextView>(R.id.titolo).text = "Cronologia di: " + extras.getString("username")
        val lista = findViewById<LinearLayout>(R.id.cronologiaLista)
        val inflater = LayoutInflater.from(this)
        lifecycleScope.launch {
            val cronologia = RemoteDAO(applicationContext, coroutineContext).getCronologia()
            for ((media, data) in cronologia) {
                val view = inflater.inflate(R.layout.cronologia_item, lista, false);

                Picasso.get().load(media.poster_path).placeholder(R.drawable.missing_poster)
                    .into(view.findViewById<ImageView>(R.id.poster))
                view.findViewById<TextView>(R.id.titoloFilm).text = media.titolo
                val dataYMD = data.split("-")
                val dataDMY = dataYMD[2] + "/" + dataYMD[1] + "/" + dataYMD[0]
                view.findViewById<TextView>(R.id.data).text = dataDMY
                view.setOnClickListener {
                    val localMedia = ConverterMedia.toLocal(this@CronologiaMedia, media, false, this@CronologiaMedia)
                    val intent = Intent(this@CronologiaMedia, AggiungiMedia::class.java)
                    intent.putExtra("id", media.mediaID)
                    intent.putExtra("titoloMedia", media.titolo)
                    intent.putExtra("poster", media.poster_path)
                    intent.putExtra("isFilm", media.is_film)
                    intent.putExtra("sinossi", media.sinossi)
                    intent.putExtra("platforms", Converters().platformToString(localMedia.platform))
                    startActivity(intent)

                }
                lista.addView(view)
            }
        }
    }
}