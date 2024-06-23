package unitn.app.activities.profilo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.test.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.activities.homepage.HomePage
import unitn.app.localdb.UserDatabase


class Profilo : AppCompatActivity() {

    private val viewFragAdapter = ViewPagerFragmentAdapter(this);
    private var currentTab = 0;
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
        val buttonSave = findViewById<ImageButton>(R.id.buttonSave)

        //Change colors
        LiveDatas.liveColore.observe(this) {
            LiveDatas.updateColorsOfImgButtons(listOf(buttonSave))
        }

        lifecycleScope.launch {
            val userDao = Room.databaseBuilder(
                applicationContext,
                UserDatabase::class.java, "user-db"
            ).fallbackToDestructiveMigration()
                .build().userDao()
            val username = userDao.getUserId()!!;
            //nome
            textNomeUtente.text = "Ciao, $username!"
        }

        //button to save
        buttonSave.setOnClickListener {
            startActivity(Intent(this@Profilo, HomePage::class.java))
            finish()
        }

        val mediaSelected = findViewById<TabLayout>(R.id.pageSelection);
        val viewPager = findViewById<ViewPager2>(R.id.pager)
        viewPager.adapter = viewFragAdapter;

        TabLayoutMediator(mediaSelected, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "INFO";
                }
                1 -> {
                    tab.text = "TEMA";
                }
                2 -> {
                    tab.text = "ACCOUNT";
                }
                3 -> {
                    tab.text = "STORICO";
                }
            }
        }.attach()

        mediaSelected.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTab = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }
}