package unitn.app.activities.profilo

import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.remotedb.Colori
import unitn.app.remotedb.RemoteDAO
import java.io.IOException

class ProfiloSelezioneTema : AppCompatActivity() {

    private lateinit var activeButton: ImageButton;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profilo_selezione_tema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnAzzuro = findViewById<ImageButton>(R.id.btnAzzurro)
        val btnVerde = findViewById<ImageButton>(R.id.btnVerde)
        val btnViola = findViewById<ImageButton>(R.id.btnViola)
        val btnOcra = findViewById<ImageButton>(R.id.btnOcra)

        val temaChiaro = findViewById<CheckBox>(R.id.temaChiaro)
        val temaScuro = findViewById<CheckBox>(R.id.temaScuro)

        //colors
        val colori = Colori.getColori();
        val lastColor = colori.indexOf(colori.find { it == LiveDatas.getColore() });


        activeButton = when (lastColor) {
            0 -> btnAzzuro
            1 -> btnVerde
            2 -> btnViola
            3 -> btnOcra
            else -> throw IOException("Active button not found")
        };
        activeButton.background = ContextCompat.getDrawable(this, R.drawable.selected_circle);

        listenToBtnClick(btnAzzuro, colori[0]);
        listenToBtnClick(btnVerde, colori[1]);
        listenToBtnClick(btnViola, colori[2]);
        listenToBtnClick(btnOcra, colori[3]);


        val isDarkTheme = LiveDatas.liveIsDarkTheme.value!!
        if (isDarkTheme) {
            temaChiaro.isChecked = false;
            temaScuro.isChecked = true;
        } else {
            temaChiaro.isChecked = true;
            temaScuro.isChecked = false;
        }

        temaChiaro.setOnClickListener {
            LiveDatas.updateIsDarkTheme(false, this)
            temaChiaro.isChecked = true;
            temaScuro.isChecked = false;
        }
        temaScuro.setOnClickListener {
            LiveDatas.updateIsDarkTheme(true, this)
            temaScuro.isChecked = true;
            temaChiaro.isChecked = false;
        }
    }

    private fun listenToBtnClick(button: ImageButton, color: Colori) {
        button.setOnClickListener {
            lifecycleScope.launch {
                RemoteDAO(this@ProfiloSelezioneTema, coroutineContext).updateColor(color.colorName)
            }
            LiveDatas.setColore(color.colorName)
            button.background = ContextCompat.getDrawable(this, R.drawable.selected_circle);
            activeButton.background = ContextCompat.getDrawable(this, R.drawable.circle);
            activeButton = button;
        }
    }

}