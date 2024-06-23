package unitn.app.activities.profilo

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.remotedb.RemoteDAO

class FragmentPiattaforme : Fragment() {
    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_piattaforme, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val urlSito = view.findViewById<TextView>(R.id.URLsito)

        //url sito
        urlSito.movementMethod = LinkMovementMethod.getInstance()


        //piattaforme
        setPiattaForme(view)

    }
    private fun setPiattaForme(view: View) {
        val netflix = view.findViewById<CheckBox>(R.id.netflixBox)
        val amazon = view.findViewById<CheckBox>(R.id.amazonPrimeBox)
        val raiplay = view.findViewById<CheckBox>(R.id.raiPlayBox)
        val disneyplus = view.findViewById<CheckBox>(R.id.disneyPlusBox)
        val crunchyroll = view.findViewById<CheckBox>(R.id.crunchyrollBox)
        lifecycleScope.launch {
            val piattaforme = RemoteDAO(view.context, coroutineContext).getPiattaformeUser()
                .map { it.nome };
            if (piattaforme.contains("Netflix")) {
                netflix.isChecked = true;
            }
            if (piattaforme.contains("Amazon Prime Video")) {
                amazon.isChecked = true;
            }
            if (piattaforme.contains("Rai Play")) {
                raiplay.isChecked = true;
            }
            if (piattaforme.contains("Disney Plus")) {
                disneyplus.isChecked = true;
            }
            if (piattaforme.contains("Crunchyroll")) {
                crunchyroll.isChecked = true;
            }
        }.invokeOnCompletion {
            detectStateCheckBox(netflix, "Netflix", view.context)
            detectStateCheckBox(amazon, "Amazon Prime Video", view.context)
            detectStateCheckBox(raiplay, "Rai Play", view.context)
            detectStateCheckBox(disneyplus, "Disney Plus", view.context)
            detectStateCheckBox(crunchyroll, "Crunchyroll", view.context)
        }
    }
    private fun detectStateCheckBox(box: CheckBox, nomePiattaforma: String, context: Context) {
        box.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                if (isChecked) {
                    RemoteDAO(context, coroutineContext).insertPiattaformaAdUser(
                        nomePiattaforma
                    )
                } else {
                    RemoteDAO(context, coroutineContext).removePiattaformaAdUser(
                        nomePiattaforma
                    )
                }
            }
        }
    }
}