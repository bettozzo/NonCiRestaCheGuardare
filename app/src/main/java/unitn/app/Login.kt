package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.runBlocking
import unitn.app.localdb.UserDatabase
import unitn.app.remotedb.RemoteDAO

class Login : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        LiveDatas.setIsDarkTheme(false)
        LiveDatas.liveIsDarkTheme.observe(this) {
            if (it) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        LiveDatas.emptyMedia();

        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val username = findViewById<EditText>(R.id.username)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signInButton = findViewById<Button>(R.id.signInButton)


        val userDao = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java, "user-db"
        ).fallbackToDestructiveMigration()
            .build().userDao()

        runBlocking {
            val isAlreadyAuth = userDao.getUserId() != null
            if (isAlreadyAuth) {
                val intent = Intent(this@Login, HomePage::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }


        loginButton.setOnClickListener {
            runBlocking {
                val userid = username.text.toString();
                val isValid = RemoteDAO.initUser(userid) != null;
                if (isValid) {
                    userDao.deleteEvertyhing();
                    userDao.insertUser(userid);

                    val intent = Intent(this@Login, HomePage::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Username non valido", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }


        signInButton.setOnClickListener{
            val userid = username.text.toString();
            val intent = Intent(this@Login, SignIn::class.java)
            intent.putExtra("userid", userid)
            startActivity(intent)
        }
    }
}