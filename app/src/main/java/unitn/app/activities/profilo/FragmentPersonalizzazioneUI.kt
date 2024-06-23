package unitn.app.activities.profilo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.remotedb.RemoteDAO


class FragmentPersonalizzazioneUI : Fragment() {

    private var root: View? = null

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

        val spinnerColors = view.findViewById<Spinner>(R.id.chooseColor)
        val switchTema = view.findViewById<SwitchCompat>(R.id.switchTema)


        //colors
        val colori = arrayOf(
            Pair("Azzurro", "#2d95eb"),
            Pair("Verde", "#008c00"),
            Pair("Viola", "#852deb"),
        );
        val lastColor = 0;

        spinnerColors.adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, colori.map { it.first });
        spinnerColors.setSelection(if (lastColor != -1) lastColor else 0)


        spinnerColors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long,
            ) {
                lifecycleScope.launch {
                    RemoteDAO(view!!.context, coroutineContext).updateColor(colori[position].first)
                }
                LiveDatas.setColore(colori[position].second)
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        switchTema.isChecked = LiveDatas.liveIsDarkTheme.value!!
        switchTema.setOnCheckedChangeListener { _, isChecked ->
            LiveDatas.setIsDarkTheme(isChecked)
            lifecycleScope.launch {
                RemoteDAO(view.context, coroutineContext).updateDarkTheme(switchTema.isChecked)
            }
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

}