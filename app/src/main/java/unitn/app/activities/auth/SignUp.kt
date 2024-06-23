package unitn.app.activities.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.runBlocking
import unitn.app.activities.profilo.Profilo
import unitn.app.localdb.UserDatabase
import unitn.app.remotedb.RemoteDAO

class SignUp : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userid = intent.extras!!.getString("userid", "")

        val username = findViewById<EditText>(R.id.username)
        val button = findViewById<Button>(R.id.signInButton)

        username.setText(userid);


        button.setOnClickListener {
            val userDao = Room.databaseBuilder(
                applicationContext,
                UserDatabase::class.java, "user-db"
            ).fallbackToDestructiveMigration()
                .build().userDao()

            runBlocking {
                val userId = username.text.toString().trim();
                var count = 0;
                var hasWeirdChar = false;
                for (char in userId) {
                    count++;
                    if (",.-'\"!?%#=()[]{}@/\\*+".contains(char)) {
                        hasWeirdChar = true;
                    }
                }

                val isNotPresent = RemoteDAO.getUser(userId) == null;
                val isNotTooLong = count < 16;

                val isValid = isNotPresent && isNotTooLong && !hasWeirdChar
                if (isValid) {
                    RemoteDAO.insertUser(userId)
                    userDao.insertUser(userId);
                    val intent = Intent(this@SignUp, Profilo::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val messaggioErrore = if (!isNotPresent) {
                        "Nome già presente"
                    } else {
                        "Nome non può essere più lungo di 15 caratteri o avere caratteri speciali"
                    }

                    val builder = AlertDialog.Builder(this@SignUp)
                    with(builder) {
                        setTitle("Nome non valido")
                        setMessage(messaggioErrore)
                        setPositiveButton(android.R.string.ok, null)
                        show()
                    }
                }
            }
        }
    }
}