package unitn.app.activities.profilo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.remotedb.Colori
import unitn.app.remotedb.RemoteDAO


class FragmentPersonalizzazioneUI : Fragment() {

    private var root: View? = null
    private lateinit var activeButton: ImageButton;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_personalizzazione_u_i, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAzzuro = view.findViewById<ImageButton>(R.id.btnAzzurro)
        val btnVerde = view.findViewById<ImageButton>(R.id.btnVerde)
        val btnViola = view.findViewById<ImageButton>(R.id.btnViola)

        val temaChiaro = view.findViewById<CheckBox>(R.id.temaChiaro)
        val temaScuro = view.findViewById<CheckBox>(R.id.temaScuro)

        //colors
        val colori = arrayOf(
            Colori("Azzurro", "#2d95eb"),
            Colori("Verde", "#008c00"),
            Colori("Viola", "#852deb"),
        );
        val lastColor = colori.indexOf(colori.find { it.colorCode == LiveDatas.getColore() });


        activeButton = when (lastColor) {
            0 -> {
                btnAzzuro
            }
            1 -> {
                btnVerde
            }
            else -> {
                btnViola
            }
        };
        activeButton.background = ContextCompat.getDrawable(view.context, R.drawable.selected_circle);

        listenToBtnClick(btnAzzuro, colori[0], view.context);
        listenToBtnClick(btnVerde, colori[1], view.context);
        listenToBtnClick(btnViola, colori[2], view.context);


        val isDarkTheme = LiveDatas.liveIsDarkTheme.value!!
        if(isDarkTheme){
            temaChiaro.isChecked = false;
            temaScuro.isChecked = true;
        }else{
            temaChiaro.isChecked = true;
            temaScuro.isChecked = false;
        }

        temaChiaro.setOnClickListener{
            LiveDatas.updateIsDarkTheme(false, view.context)
            temaChiaro.isChecked = true;
            temaScuro.isChecked = false;
        }
        temaScuro.setOnClickListener{
            LiveDatas.updateIsDarkTheme(true, view.context)
            temaScuro.isChecked = true;
            temaChiaro.isChecked = false;
        }
    }

    private fun listenToBtnClick(button: ImageButton, color: Colori, context: Context) {
        button.setOnClickListener {
            lifecycleScope.launch {
                RemoteDAO(context, coroutineContext).updateColor(color.colorName)
            }
            LiveDatas.setColore(color.colorCode)
            button.background =  ContextCompat.getDrawable(context, R.drawable.selected_circle);
            activeButton.background = ContextCompat.getDrawable(context, R.drawable.circle);
            activeButton = button;
        }
    }

}