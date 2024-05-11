package unitn.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test.R
import com.squareup.picasso.Picasso

class DettaglioFilm : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dettaglio_film)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val extras = intent.extras
        if (extras == null) {
            System.err.println("Bundle is null")
            return;
        }
        findViewById<TextView>(R.id.titoloFilm).text = extras.getString("titoloFilm")
        Picasso.get().load(extras.getString("poster")).placeholder(R.drawable.missing_poster)
            .into(findViewById<ImageView>(R.id.poster))
        // TODO piattaforme
        // TODO switch

    }
}