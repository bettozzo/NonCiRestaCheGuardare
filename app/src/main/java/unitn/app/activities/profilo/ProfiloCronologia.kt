package unitn.app.activities.profilo

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

        populateCronologia();

    }

    private fun populateCronologia() {
        val lista = findViewById<LinearLayout>(R.id.cronologiaLista)
        val inflater = LayoutInflater.from(this@ProfiloCronologia)
        lifecycleScope.launch {
            val cronologia = RemoteDAO(this@ProfiloCronologia, coroutineContext).getCronologia()
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