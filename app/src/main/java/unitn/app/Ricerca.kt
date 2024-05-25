package unitn.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.test.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unitn.app.api.MediaDetails


class Ricerca : AppCompatActivity() {
    private lateinit var mediaDetails: MediaDetails;

    private var lastTitleQueried: String? = null;

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
        val searchBar = findViewById<EditText>(R.id.searchBar)
        searchBar.requestFocus()
        val buttonToSearch = findViewById<Button>(R.id.buttonToSearch)

        mediaDetails.liveListMedia.observe(this) {
            gridView.adapter = AdapterSearch(this@Ricerca, it)
        }
        mediaDetails.liveNoInternet.observe(this) {
            if (it) {
                android.app.AlertDialog.Builder(this@Ricerca).setTitle("NO INTERNET")
                    .setMessage("Per usare questa funzionalità è richiesta la connessiona ad internet")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        finish()
                    }.show()
            }
        }
        LiveDatas.liveColore.observe(this) {
            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled), // enabled
                intArrayOf(-android.R.attr.state_enabled), // disabled
                intArrayOf(-android.R.attr.state_checked), // unchecked
                intArrayOf(android.R.attr.state_pressed)  // pressed
            )
            val colors = intArrayOf(
                Color.parseColor(it),
                Color.parseColor(it),
                Color.parseColor(it),
                Color.parseColor(it),
            )
            buttonToSearch.backgroundTintList = ColorStateList(states, colors)
        }

        buttonToSearch.setOnClickListener {
            val title = searchBar.text.toString();
            callAPI(title, lastTitleQueried)
            lastTitleQueried = title;
        }

        searchBar.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val title = searchBar.text.toString();
                callAPI(title, lastTitleQueried)
                lastTitleQueried = title;
                return@setOnKeyListener true;
            }
            if (event.isLongPress && keyCode == KeyEvent.KEYCODE_DEL) {
                searchBar.text.clear()
            }
            return@setOnKeyListener false;
        }
    }

    override fun onResume() = runBlocking {
        super.onResume()
        mediaDetails.updateMediaList();
    }

    private fun callAPI(title: String, lastQuery: String?) {
        val searchBar = findViewById<EditText>(R.id.searchBar)
        val apiKey = resources.getString(R.string.api_key_tmdb)
        //prevents concurrency problems
        if (lastQuery != title) {
            CoroutineScope(Dispatchers.IO).launch {
                val foundSomething = mediaDetails.getDetails(title, apiKey);
                runOnUiThread {
                    if (!foundSomething) {
                        AlertDialog.Builder(this@Ricerca)
                            .setTitle("Nessun risultato.")
                            .setMessage("Cerca un'altro titolo!")
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                searchBar.requestFocus();
                                searchBar.showKeyboard();
                            }.show()
                    }
                }
            }
            searchBar.hideKeyboard()
        } else {
            Toast.makeText(this, "Ricerca appena effettuata", Toast.LENGTH_SHORT).show();
        }
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}