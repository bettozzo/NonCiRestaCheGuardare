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
import androidx.lifecycle.ViewModelProvider
import com.example.test.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unitn.app.api.MediaDetails


class Ricerca : AppCompatActivity() {
    private lateinit var mediaDetails: MediaDetails;

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ricerca)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mediaDetails = ViewModelProvider(this)[MediaDetails::class.java];

        val gridView = findViewById<GridView>(R.id.GridView)
        val searchBar = findViewById<EditText>(R.id.Base)

        val buttonToSearch = findViewById<Button>(R.id.buttonToSearch)
        val apiKey = resources.getString(R.string.api_key_tmdb)

        mediaDetails.liveListMedia.observe(this) {
            gridView.adapter = AdapterSearch(this@Ricerca, it)
        }

        var lastTitleQueried: String? = null;
        buttonToSearch.setOnClickListener {
            val currentQuery = searchBar.text.toString();
            //prevents concurrency problems
            if (lastTitleQueried != currentQuery) {
                lastTitleQueried = currentQuery;
                CoroutineScope(Dispatchers.IO).launch {
                    mediaDetails.getDetails(currentQuery, apiKey);
                }
            }
        }
    }
}
