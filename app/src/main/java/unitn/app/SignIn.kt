package unitn.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.runBlocking
import unitn.app.localdb.UserDatabase
import unitn.app.remotedb.RemoteDAO

class SignIn : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
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
                val userId = username.text.toString();
                val isValid = RemoteDAO.initUser(userId) == null;
                if(isValid) {
                    RemoteDAO.insertUser(userId)
                    userDao.insertUser(userId);
                    val intent = Intent(this@SignIn, HomePage::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(applicationContext, "Username non valido!!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}