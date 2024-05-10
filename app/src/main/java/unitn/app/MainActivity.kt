package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test.R
import com.squareup.picasso.Picasso
import unitn.app.api.Movies


data class GridViewModal(
    val courseName: String,
    val courseImg: Int
)


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val goToSearchButton = findViewById<Button>(R.id.goToSearchMediaButton)

        goToSearchButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchForMedia::class.java)
            startActivity(intent)
        }

        val gridView = findViewById<GridView>(R.id.GridView)

//        val imageView = findViewById<ImageView>(R.id.imageView)
//        Picasso.get().load("https://i.imgur.com/DvpvklR.png").into(imageView);

        val movies = emptyList<Movies>().toMutableList()
        movies.add(Movies(1, "Killo", emptyList<String>(), "https://image.tmdb.org/t/p/w300/z60RmobbwAwl71ueZss4E1OXzNc.jpg"))
        movies.add(Movies(2, "Billo", emptyList<String>(), "https://image.tmdb.org/t/p/w300/1l0Wg9Gw6rHvSLm79tnqIHSKbWY.jpg"))
        movies.add(Movies(3, "ABCDEFGHIJKLMNO", emptyList<String>(), "https://image.tmdb.org/t/p/w300/nxbv9TPXrIpRKB5xQh6atWXAkPM.jpg"))
        gridView.adapter = CustomAdapter(this@MainActivity, movies)
    }
}
