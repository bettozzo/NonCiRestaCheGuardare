package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.localdb.Converters
import unitn.app.localdb.MoviesDatabase


class HomePage : AppCompatActivity() {
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

        val goToSearchButton = findViewById<ImageButton>(R.id.goToSearchMediaButton)

        goToSearchButton.setOnClickListener {
            val intent = Intent(this@HomePage, Search::class.java)
            startActivity(intent)
        }

        val gridView = findViewById<GridView>(R.id.GridView)

        lifecycleScope.launch {
            val movieDao = Room.databaseBuilder(
                applicationContext,
                MoviesDatabase::class.java, "database-name"
            ).addTypeConverter(Converters())
                .build().movieDao()
            val movies = movieDao.getAll()
            gridView.adapter = AdapterHomepage(this@HomePage, movies)
        }

    }

    override fun onResume() {
        super.onResume()

        val gridView = findViewById<GridView>(R.id.GridView)
        lifecycleScope.launch {
            val movieDao = Room.databaseBuilder(
                applicationContext,
                MoviesDatabase::class.java, "database-name"
            ).addTypeConverter(Converters())
                .build().movieDao()
            val movies = movieDao.getAll()
            gridView.adapter = AdapterHomepage(this@HomePage, movies)
        }
    }
}
