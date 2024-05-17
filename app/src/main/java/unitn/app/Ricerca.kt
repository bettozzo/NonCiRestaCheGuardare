package unitn.app

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.test.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unitn.app.api.MediaDetails
import unitn.app.api.Media


class Ricerca : AppCompatActivity() {
    private lateinit var mediaDetails: MediaDetails;
    private var mediaBeingSearched = getQueriedMedia();
    private var sharedPref: SharedPreferences? = null

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
        sharedPref = this.getSharedPreferences("queriedMedia", MODE_PRIVATE)

        val gridView = findViewById<GridView>(R.id.GridView)
        val searchBar = findViewById<EditText>(R.id.Base)

        val buttonToSearch = findViewById<Button>(R.id.buttonToSearch)
        val apiKey = resources.getString(R.string.api_key_tmdb)

        mediaDetails.liveListMedia.observe(this) {
            gridView.adapter = AdapterSearch(this@Ricerca, it)
        }


        buttonToSearch.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                mediaDetails.getDetails(searchBar.text.toString(), apiKey);
                val editor = sharedPref?.edit()
                if (editor != null) {
                    editor.putString("media", Converters().mediaToString(mediaBeingSearched))
                    editor.apply()
                }
            }
        }
    }
    private fun getQueriedMedia(): List<Media> {
        if (sharedPref?.contains("media") == true) {
            return Converters().stringToMedia(
                sharedPref!!.getString(
                    "media",
                    emptyList<Media>().toString()
                )!!
            ).toMutableList();
        }
        return emptyList();
    }
}


private class Converters {
    fun mediaToString(movie: List<Media>): String {
        val gson = Gson()
        return gson.toJson(movie)
    }

    fun stringToMedia(string: String): List<Media> {
        val gson = Gson()
        val listType = object : TypeToken<List<Media>>() {}.type
        return gson.fromJson(string, listType)
    }
}