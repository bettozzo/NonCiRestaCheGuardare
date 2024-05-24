package unitn.app

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.localdb.UserDatabase
import unitn.app.remotedb.RemoteDAO


class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textNomeUtente = findViewById<TextView>(R.id.titolo)
        val textPercentuale = findViewById<TextView>(R.id.contenutiVistiEDaVedere)
        val buttonGoToSeen = findViewById<Button>(R.id.buttonGoToSeen)
        val spinnerColors = findViewById<Spinner>(R.id.chooseColor)
        val buttonSave = findViewById<ImageButton>(R.id.buttonSave)

        //nome

        lifecycleScope.launch {
            val userDao = Room.databaseBuilder(
                applicationContext,
                UserDatabase::class.java, "user-db"
            ).fallbackToDestructiveMigration()
                .build().userDao()
            val username = userDao.getUserId();
            textNomeUtente.text = "Ciao, $username!"
        }
        //colors
        var nuovoColore: String? = null;
        val colori = arrayOf("Verde", "Viola", "Azzurro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, colori)
        spinnerColors.adapter = adapter;
        spinnerColors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                nuovoColore = colori[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {

            }
        }


        //button to save
        buttonSave.setOnClickListener {
            lifecycleScope.launch {
                if (nuovoColore != null) {
                    RemoteDAO(this@Profile, coroutineContext).insertColor(nuovoColore!!)
                }
                finish();
            }
        }
    }
}





















