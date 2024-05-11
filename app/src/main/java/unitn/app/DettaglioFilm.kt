package unitn.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import android.widget.LinearLayout
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
        val extras = intent.extras;
        if (extras == null) {
            System.err.println("Bundle is null")
            return;
        }
        val titotloFilm = findViewById<TextView>(R.id.titoloFilm);
        titotloFilm.text = extras.getString("titoloFilm")
        titotloFilm.ellipsize = TextUtils.TruncateAt.MARQUEE;
        titotloFilm.marqueeRepeatLimit = -1;

        Picasso.get().load(extras.getString("poster")).placeholder(R.drawable.missing_poster)
            .into(findViewById<ImageView>(R.id.poster))

        val plaformList = findViewById<LinearLayout>(R.id.plaformList)
        val netflixLogo = extras.getString("NetflixPath")
        val primevideoLogo = extras.getString("AmazonPath")
        val disneyplusLogo = extras.getString("DisneyPath")

        if (netflixLogo != null) {
            val netflixView = ImageView(this);
            Picasso.get().load(netflixLogo).into(netflixView);
            plaformList.addView(netflixView);
        }
        if (primevideoLogo != null) {
            val primeView = ImageView(this);
            Picasso.get().load(primevideoLogo).into(primeView);
            plaformList.addView(primeView);
        }
        if (disneyplusLogo != null) {
            val disneyView = ImageView(this);
            Picasso.get().load(disneyplusLogo).into(disneyView);
            plaformList.addView(disneyView);
        }

        if (netflixLogo == null && primevideoLogo == null && disneyplusLogo == null) {
            val noProviders = ImageView(this);
            Picasso.get().load(R.drawable.no_providers).into(noProviders);
            plaformList.addView(noProviders);
        }
        // TODO switch
    }
}