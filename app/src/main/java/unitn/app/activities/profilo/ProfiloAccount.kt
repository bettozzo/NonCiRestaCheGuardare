package unitn.app.activities.profilo

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.localdb.UserDatabase
import unitn.app.remotedb.RemoteDAO

class ProfiloAccount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profilo_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usernameView = findViewById<EditText>(R.id.username)
        val buttonSaveUsername = findViewById<Button>(R.id.btnSaveUsername)



        //piattaforme
        setPiattaForme()

        //username
        lifecycleScope.launch {
            val localDao = Room.databaseBuilder(
                this@ProfiloAccount,
                UserDatabase::class.java, "user-db"
            ).fallbackToDestructiveMigration()
                .build().userDao()
            val username = localDao.getUserId()!!;
            usernameView.text = SpannableStringBuilder(username)
        }
        //save button username
        setSaveUsernameButton(buttonSaveUsername, usernameView);
    }

    //textView needs to be EditText, so it can fetch current username at click time
    private fun setSaveUsernameButton(buttonSave: Button, textView: EditText) {
        LiveDatas.liveColore.observe(this) {
            LiveDatas.updateColorsOfButtons(listOf(buttonSave))
        }
        buttonSave.setOnClickListener {
            lifecycleScope.launch {
                val remoteDao = RemoteDAO(this@ProfiloAccount, coroutineContext);
                val newUsername = textView.text.toString()
                val isAvailable = remoteDao.updateUser(newUsername, this@ProfiloAccount);
                if (!isAvailable) {
                    Toast.makeText(this@ProfiloAccount, "Nome non disponibile", Toast.LENGTH_SHORT).show();
                } else {
                    //new username is already updated in DB
                    findViewById<TextView>(R.id.titolo).text = "Ciao, $newUsername!"
                }
            }
        }
    }
    private fun setPiattaForme() {
        val netflix = findViewById<CheckBox>(R.id.netflixBox)
        val amazon = findViewById<CheckBox>(R.id.amazonPrimeBox)
        val raiplay = findViewById<CheckBox>(R.id.raiPlayBox)
        val disneyplus = findViewById<CheckBox>(R.id.disneyPlusBox)
        val crunchyroll = findViewById<CheckBox>(R.id.crunchyrollBox)
        lifecycleScope.launch {
            val piattaforme = RemoteDAO(this@ProfiloAccount, coroutineContext).getPiattaformeUser()
                .map { it.nome };
            if (piattaforme.contains("Netflix")) {
                netflix.isChecked = true;
            }
            if (piattaforme.contains("Amazon Prime Video")) {
                amazon.isChecked = true;
            }
            if (piattaforme.contains("Rai Play")) {
                raiplay.isChecked = true;
            }
            if (piattaforme.contains("Disney Plus")) {
                disneyplus.isChecked = true;
            }
            if (piattaforme.contains("Crunchyroll")) {
                crunchyroll.isChecked = true;
            }
        }.invokeOnCompletion {
            detectStateCheckBox(netflix, "Netflix")
            detectStateCheckBox(amazon, "Amazon Prime Video")
            detectStateCheckBox(raiplay, "Rai Play")
            detectStateCheckBox(disneyplus, "Disney Plus")
            detectStateCheckBox(crunchyroll, "Crunchyroll")
        }
    }
    private fun detectStateCheckBox(box: CheckBox, nomePiattaforma: String) {
        box.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                if (isChecked) {
                    RemoteDAO(this@ProfiloAccount, coroutineContext).insertPiattaformaAdUser(
                        nomePiattaforma
                    )
                } else {
                    RemoteDAO(this@ProfiloAccount, coroutineContext).removePiattaformaAdUser(
                        nomePiattaforma
                    )
                }
            }
        }
    }
}