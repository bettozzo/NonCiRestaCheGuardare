package unitn.app

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unitn.app.localdb.UserDatabase
import unitn.app.remotedb.RemoteDAO


class Profilo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profilo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textNomeUtente = findViewById<TextView>(R.id.titolo)
        val textPercentuale = findViewById<TextView>(R.id.contenutiVistiEDaVedere)
        val buttonGoToCronologia = findViewById<Button>(R.id.buttonGoToSeen)
        val urlSito = findViewById<TextView>(R.id.URLsito)
        val spinnerColors = findViewById<Spinner>(R.id.chooseColor)
        val buttonLogOff = findViewById<Button>(R.id.buttonLogOff)
        val buttonDeleteAccount = findViewById<Button>(R.id.buttonDeleteAccount)
        val buttonSave = findViewById<ImageButton>(R.id.buttonSave)


        //nome
        setName(textNomeUtente);

        //completismo
        setCompletamento(textPercentuale)

        //cronologia
        buttonGoToCronologia.setOnClickListener {
            startActivity(Intent(this@Profilo, CronologiaMedia::class.java))
        }

        //url sito
        urlSito.movementMethod = LinkMovementMethod.getInstance()

        //colors
        var nuovoColore: String? = null;
        val colori = arrayOf("Verde", "Viola", "Azzurro")
        val coloriCode = arrayOf("#008c00", "#852deb", "#2d95eb")
        val lastColor = coloriCode.indexOf(getCurrentColor());

        spinnerColors.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, colori);
        spinnerColors.setSelection(if (lastColor != -1) lastColor else 0)

        spinnerColors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long,
            ) {
                nuovoColore = colori[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        //button log off
        setLogOffButton(buttonLogOff);

        //button delete account
        setDeleteAccount(buttonDeleteAccount);

        //button to save
        buttonSave.setOnClickListener {
            lifecycleScope.launch {
                if (nuovoColore != null) {
                    RemoteDAO(this@Profilo, coroutineContext).insertColor(nuovoColore!!)
                }
                finish();
            }
        }
    }

    private fun setDeleteAccount(button: Button) {
        button.setOnClickListener {
            val builder = AlertDialog.Builder(this@Profilo)

            with(builder)
            {
                setTitle("AZIONE IRREVERSIBILE")
                setMessage("Sei sicuro di voler cancellare l'account?\nTutti i dati andranno persi in maniera irreversibile")
                setPositiveButton(android.R.string.ok) { _, _ ->
                    runBlocking {
                        val localDao = Room.databaseBuilder(
                            applicationContext,
                            UserDatabase::class.java, "user-db"
                        ).fallbackToDestructiveMigration()
                            .build().userDao()
                        val userId = localDao.getUserId()!!;
                        localDao.deleteEvertyhing();
                        RemoteDAO.deleteUser(userId);
                        val intent = Intent(this@Profilo, Login::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        finish();
                        startActivity(intent)
                    }
                }
                setNegativeButton(android.R.string.cancel, null)
                show()
            }
        }
    }

    private fun setLogOffButton(button: Button) {
        button.setOnClickListener {
            runBlocking {
                Room.databaseBuilder(
                    applicationContext,
                    UserDatabase::class.java, "user-db"
                ).fallbackToDestructiveMigration()
                    .build().userDao().deleteEvertyhing()
            }
            val intent = Intent(this@Profilo, Login::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish();
            startActivity(intent)
        }
    }

    private fun getCurrentColor(): String {
        return runBlocking {
            return@runBlocking RemoteDAO(
                applicationContext,
                coroutineContext
            ).getMainColorAsString()
        }
    }

    private fun setCompletamento(textView: TextView) {
        lifecycleScope.launch {
            val remoteDao = RemoteDAO(
                this@Profilo, coroutineContext
            );
            val cronologia = remoteDao.getAllSeenMedia()

            val filmVisti = cronologia.size;
            val filmDaVedere = remoteDao.getWatchList().size

            val totale = filmVisti + filmDaVedere
            textView.text = "$filmVisti/$totale"
        }
    }

    private fun setName(textNomeUtente: TextView) {
        lifecycleScope.launch {
            val userDao = Room.databaseBuilder(
                applicationContext,
                UserDatabase::class.java, "user-db"
            ).fallbackToDestructiveMigration()
                .build().userDao()
            val username = userDao.getUserId();
            textNomeUtente.text = "Ciao, $username!"
        }
    }
}





















