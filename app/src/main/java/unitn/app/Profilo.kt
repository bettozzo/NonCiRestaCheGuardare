package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unitn.app.localdb.UserDatabase
import unitn.app.remotedb.Colori
import unitn.app.remotedb.RemoteDAO


class Profilo : AppCompatActivity() {
    private var startingStateTheme: Boolean = false;
    @SuppressLint("MissingInflatedId")
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
        val switchTema = findViewById<SwitchCompat>(R.id.switchTema)
        val buttonLogOff = findViewById<Button>(R.id.buttonLogOff)
        val buttonDeleteAccount = findViewById<Button>(R.id.buttonDeleteAccount)
        val buttonSave = findViewById<ImageButton>(R.id.buttonSave)

        //Change colors
        LiveDatas.liveColore.observe(this) {
            LiveDatas.updateColorsOfButtons(listOf(buttonGoToCronologia, buttonLogOff))
            LiveDatas.updateColorsOfImgButtons(listOf(buttonSave))
        }

        //nome
        setName(textNomeUtente);

        //completismo
        var inPercentuale = true;
        setCompletamento(textPercentuale, inPercentuale)
        textPercentuale.setOnClickListener {
            inPercentuale = !inPercentuale
            setCompletamento(textPercentuale, inPercentuale)
        }

        //cronologia
        buttonGoToCronologia.setOnClickListener {
            startActivity(Intent(this@Profilo, CronologiaMedia::class.java))
        }

        //url sito
        urlSito.movementMethod = LinkMovementMethod.getInstance()

        //colors
        var nuovoColore: Colori? = null;
        val colori = arrayOf("Verde", "Viola", "Azzurro")
        val coloriCode = arrayOf("#008c00", "#852deb", "#2d95eb")
        val lastColor = coloriCode.indexOf(getCurrentColor());

        spinnerColors.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, colori);
        spinnerColors.setSelection(if (lastColor != -1) lastColor else 1)

        spinnerColors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long,
            ) {
                nuovoColore = Colori(colori[position], coloriCode[position])
                updateColorsOfImgButtons(coloriCode[position])
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        switchTema.isChecked = LiveDatas.liveIsDarkTheme.value!!
        startingStateTheme = switchTema.isChecked
        switchTema.setOnCheckedChangeListener { _, isChecked ->
            LiveDatas.setIsDarkTheme(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }


        //button log off
        setLogOffButton(buttonLogOff);

        //button delete account
        setDeleteAccount(buttonDeleteAccount);

        //button to save
        buttonSave.setOnClickListener {
            lifecycleScope.launch {
                if (nuovoColore != null) {
                    RemoteDAO(this@Profilo, coroutineContext).insertColor(nuovoColore!!.colorName)
                    LiveDatas.setColore(nuovoColore!!.colorCode)
                }

                val currentStateTheme = switchTema.isChecked;
                if (currentStateTheme != startingStateTheme) {
                    LiveDatas.setIsDarkTheme(currentStateTheme)
                    lifecycleScope.launch {
                        RemoteDAO(applicationContext, coroutineContext)
                            .updateDarkTheme(currentStateTheme)
                    }
                }
            }
            finish();
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val switchTema = findViewById<SwitchCompat>(R.id.switchTema)
        val currentStateTheme = switchTema.isChecked;
        if (currentStateTheme == startingStateTheme) {
            LiveDatas.setIsDarkTheme(!startingStateTheme)
            if (!startingStateTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setDeleteAccount(button: Button) {
        button.setOnClickListener {
            val builder = AlertDialog.Builder(this@Profilo)

            with(builder)
            {
                setTitle("CANCELLA ACCOUNT")
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
            ).getMainColor()
        }
    }

    private fun setCompletamento(textView: TextView, inPercentuale: Boolean) {
        lifecycleScope.launch {
            val remoteDao = RemoteDAO(
                this@Profilo, coroutineContext
            );
            val cronologia = remoteDao.getAllSeenMedia()

            val filmVisti = cronologia.size;
            val filmDaVedere = remoteDao.getWatchList().size

            val totale = filmVisti + filmDaVedere
            textView.text = if (inPercentuale) {
                String.format("%.1f", (filmVisti.toFloat() / totale.toFloat()) * 100) + "%"
            } else {
                "$filmVisti/$totale"
            }
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

    private fun updateColorsOfImgButtons(color: String) {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(-android.R.attr.state_enabled), // disabled
            intArrayOf(-android.R.attr.state_checked), // unchecked
            intArrayOf(android.R.attr.state_pressed)  // pressed
        )
        val colors = intArrayOf(
            Color.parseColor(color),
            Color.parseColor(color),
            Color.parseColor(color),
            Color.parseColor(color)
        )

        val myList = ColorStateList(states, colors)

        findViewById<Button>(R.id.buttonGoToSeen).backgroundTintList = myList
        findViewById<Button>(R.id.buttonLogOff).backgroundTintList = myList
        findViewById<ImageButton>(R.id.buttonSave).backgroundTintList = myList
    }
}