package unitn.app.activities.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.runBlocking
import unitn.app.activities.LiveDatas
import unitn.app.activities.loadingScreen.LoadingScreen
import unitn.app.localdb.UserDatabase
import unitn.app.remotedb.RemoteDAO
import unitn.app.remotedb.VersionInfo

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


        if (!currentVersionIsCompatible()) {
            return;
        };

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
                val intent = Intent(this@Login, LoadingScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }


        loginButton.setOnClickListener {
            runBlocking {
                val userid = username.text.toString().trim();
                val isValid = RemoteDAO.getUser(userid) != null;
                if (isValid) {
                    userDao.deleteEvertyhing();
                    userDao.insertUser(userid);

                    val intent = Intent(this@Login, LoadingScreen::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Username non valido", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }


        signInButton.setOnClickListener {
            val userid = username.text.toString().trim();
            val intent = Intent(this@Login, SignUp::class.java)
            intent.putExtra("userid", userid)
            startActivity(intent)
        }
    }

    private fun currentVersionIsCompatible(): Boolean {
        val currentVersion = VersionInfo(2, 1, 0);

        val latestVersion: VersionInfo;
        runBlocking {
            latestVersion = RemoteDAO.getRightVersionApp();
        }
        if (currentVersion.major_version != latestVersion.major_version) {
            val normal = findViewById<RelativeLayout>(R.id.main)
            val updateAlert = findViewById<LinearLayout>(R.id.updateInfo)
            val versionInfoView = findViewById<TextView>(R.id.versionText)
            normal.visibility = View.GONE;
            updateAlert.visibility = View.VISIBLE;
            versionInfoView.text = latestVersion.toString();
            return false;
        }
        if (currentVersion.minor_version != latestVersion.minor_version) {
            Toast.makeText(
                this,
                "Nuova versione disponibile\nV$latestVersion",
                Toast.LENGTH_LONG
            ).show();
            return true;
        }
        if (currentVersion.patch_version != latestVersion.patch_version) {
            Toast.makeText(
                this,
                "Nuova patch disponibile\nV$latestVersion",
                Toast.LENGTH_SHORT
            ).show();
            return true;
        }
        return true;
    }
}
