package unitn.app.test

import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.NonCiRestaCheGuardare.api.MediaDetails
import com.example.NonCiRestaCheGuardare.api.Movies
import com.example.test.R

class SearchForMedia : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_for_media)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//TODO AsyncTask, questo forza i thread a stare zitti
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        var films: List<Movies>
        val results = findViewById<TextView>(R.id.Results)
        val searchBar = findViewById<EditText>(R.id.Base)

        val buttonToSearch = findViewById<Button>(R.id.buttonToSearch)
        buttonToSearch.setOnClickListener {
            films = MediaDetails().getDetails(searchBar.text.toString())
            if (films.isNotEmpty()) {
                val stringBuilder: StringBuilder = StringBuilder("")
                for (film in films) {
                    stringBuilder.append(film.toString()).append("\n")
                }
                results.text = stringBuilder
            } else {
                results.text = "NONE!!!"
            }
        }
    }
}