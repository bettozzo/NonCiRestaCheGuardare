package unitn.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unitn.app.api.MediaDetails
import unitn.app.api.Movies
import java.io.Serializable


class Search : AppCompatActivity() {
    private var moviesBeingSearched = emptyList<Movies>()
    private var test = "ciao";

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val gridView = findViewById<GridView>(R.id.GridView)
        val searchBar = findViewById<EditText>(R.id.Base)

        val buttonToSearch = findViewById<Button>(R.id.buttonToSearch)
        val apiKey = resources.getString(R.string.api_key_tmdb)


        buttonToSearch.setOnClickListener {
            gridView.adapter = AdapterHomepage(this@Search, emptyList())

            CoroutineScope(Dispatchers.IO).launch {
                moviesBeingSearched = MediaDetails().getDetails(searchBar.text.toString(), apiKey);

                runOnUiThread {
                    gridView.adapter = AdapterSearch(this@Search, moviesBeingSearched)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val gridView = findViewById<GridView>(R.id.GridView)
        gridView.adapter = AdapterSearch(this@Search, moviesBeingSearched)
    }
}

