package unitn.app.activities.profilo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.runBlocking
import unitn.app.activities.auth.Login
import unitn.app.localdb.UserDatabase
import unitn.app.remotedb.RemoteDAO


class Profilo : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profilo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textNomeUtente = findViewById<TextView>(R.id.titolo)

        val username: String;
        runBlocking {
            val userDao = Room.databaseBuilder(
                applicationContext,
                UserDatabase::class.java, "user-db"
            ).fallbackToDestructiveMigration()
                .build().userDao()
            username = userDao.getUserId()!!;
        }

        //nome
        textNomeUtente.text = "Ciao, $username!"


        val selezioneTema = findViewById<LinearLayout>(R.id.GoToSelezioneTema)
        val account = findViewById<LinearLayout>(R.id.GoToAccount)
        val cronologia = findViewById<LinearLayout>(R.id.GoToCronologia)
        val statistiche = findViewById<LinearLayout>(R.id.GoToStatistiche)

        selezioneTema.onClickGoToActivity(ProfiloSelezioneTema::class.java)
        account.onClickGoToActivity(ProfiloAccount::class.java)
        cronologia.onClickGoToActivity(ProfiloCronologia::class.java)
        statistiche.onClickGoToActivity(ProfiloStatistiche::class.java)




        //url sito
        val urlSito = findViewById<TextView>(R.id.URLsito)
        urlSito.movementMethod = LinkMovementMethod.getInstance()


        //button log off
        val btnLogOff = findViewById<LinearLayout>(R.id.buttonLogOff)
        setLogOffButton(btnLogOff);

        //button delete account
        val btnDelete = findViewById<LinearLayout>(R.id.buttonDeleteAccount)
        setDeleteAccount(btnDelete);
    }

    private fun<T: Any> LinearLayout.onClickGoToActivity(target: Class<T>){
        this.setOnClickListener {
            val intent = Intent(this@Profilo,target );
            startActivity(intent)
        }
    }
    private fun setDeleteAccount(button: LinearLayout) {
        button.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            with(builder)
            {
                setTitle("CANCELLA ACCOUNT")
                setMessage("Sei sicuro di voler cancellare l'account?\nTutti i dati andranno persi in maniera irreversibile")
                setPositiveButton(android.R.string.ok) { _, _ ->
                    runBlocking {
                        val localDao = Room.databaseBuilder(
                            context,
                            UserDatabase::class.java, "user-db"
                        ).fallbackToDestructiveMigration()
                            .build().userDao()
                        val userId = localDao.getUserId()!!;
                        localDao.deleteEvertyhing();
                        RemoteDAO.deleteUser(userId);
                        val intent = Intent(context, Login::class.java)
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

    private fun setLogOffButton(button: LinearLayout) {
        button.setOnClickListener {
            runBlocking {
                Room.databaseBuilder(
                    this@Profilo,
                    UserDatabase::class.java, "user-db"
                ).fallbackToDestructiveMigration()
                    .build().userDao().deleteEvertyhing()
            }
            val intent = Intent(this, Login::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish();
            startActivity(intent)
        }
    }
}