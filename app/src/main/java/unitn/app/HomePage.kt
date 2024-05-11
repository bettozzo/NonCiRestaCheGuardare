package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test.R
import unitn.app.api.Movies


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val goToSearchButton = findViewById<Button>(R.id.goToSearchMediaButton)

        goToSearchButton.setOnClickListener {
            val intent = Intent(this@MainActivity, Search::class.java)
            startActivity(intent)
        }

        val gridView = findViewById<GridView>(R.id.GridView)


        val movies = emptyList<Movies>().toMutableList()
        movies.add(
            Movies(
                1,
                "Killo",
                emptyList(),
                "https://image.tmdb.org/t/p/w300/z60RmobbwAwl71ueZss4E1OXzNc.jpg"
            )
        )
        movies.add(
            Movies(
                2,
                "Billo",
                emptyList(),
                "https://image.tmdb.org/t/p/w300/1l0Wg9Gw6rHvSLm79tnqIHSKbWY.jpg"
            )
        )
        movies.add(
            Movies(
                3,
                "Killo Billo",
                emptyList(),
                "https://image.tmdb.org/t/p/w300/nxbv9TPXrIpRKB5xQh6atWXAkPM.jpg"
            )
        )
        movies.add(
            Movies(
                3,
                "ABCDEFGHJKILMNOPQRSTUVWXYZ",
                emptyList(),
                "https://image.tmdb.org/t/p/w300/8F6u12WZJPZkn81litHpCxNu26q.jpg"
            )
        )
        gridView.adapter = CustomAdapter(this@MainActivity, movies)
    }
}
