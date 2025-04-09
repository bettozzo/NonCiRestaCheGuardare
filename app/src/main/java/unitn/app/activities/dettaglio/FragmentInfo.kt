package unitn.app.activities.dettaglio

import android.app.Dialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.test.R
import com.squareup.picasso.Picasso

class FragmentInfo(private val extras: Bundle) : Fragment() {
    private var root: View? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_info, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //poster
        val poster = view.findViewById<ImageView>(R.id.poster);
        val posterPath = extras.getString("poster");
        Picasso.get().load(posterPath).placeholder(R.drawable.missing_poster)
            .into(poster)

        if (posterPath != null) {
            poster.setOnClickListener {
                val imageView = ImageView(requireActivity())
                Picasso.get().load(posterPath).placeholder(R.drawable.missing_poster)
                    .into(imageView)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                val settingsDialog = Dialog(requireActivity())
                settingsDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
                settingsDialog.addContentView(
                    imageView, ViewGroup.LayoutParams(900, 1400)
                )
                settingsDialog.show()
            }
        }

        //durata
        val durataText = view.findViewById<TextView>(R.id.durataText)
        val durata = extras.getString("durata")
        val isFilm = extras.getBoolean("isFilm")
        if (durata != null) {
            if (isFilm) {
                durataText.text = StringBuilder(durata.toString()).append(" minuti").toString()
            } else {
                val numeroStagioni = durata.split(" - ")[0]
                val numeroEpisodi = durata.split(" - ")[1]
                val txt =
                    StringBuilder("Stagioni: ").append(numeroStagioni).append("\nTot. Episodi: ")
                        .append(numeroEpisodi).append("\n")
                durataText.text = txt.toString()
            }
        } else {
            durataText.visibility = View.GONE;
            view.findViewById<LinearLayout>(R.id.infoDurata).visibility = View.GONE
        }

        //anno uscita
        val annoUscitaText = view.findViewById<TextView>(R.id.annoUscitaText)
        val annoUscita = extras.getString("annoUscita")
        if (annoUscita != null) {
            annoUscitaText.text = annoUscita
        } else {
            annoUscitaText.visibility = View.GONE;
            view.findViewById<LinearLayout>(R.id.infoAnno).visibility = View.GONE
        }


        //genere
        val genereText = view.findViewById<TextView>(R.id.genereText)
        val generi = extras.getString("generi", "")
        if (generi != "") {
            val listaGeneri = generi.split(", ");
            val txt = StringBuilder();
            for (genere in listaGeneri) {
                txt.append(genere).append("\n")
            }
            genereText.text = txt.toString()
        } else {
            genereText.visibility = View.GONE;
            view.findViewById<LinearLayout>(R.id.infoGenere).visibility = View.GONE
        }


        //sinossi
        val sinossiView = view.findViewById<TextView>(R.id.sinossiText)
        val sinossi = extras.getString("sinossi", "no sinossi");
        sinossiView.text = sinossi;
        sinossiView.movementMethod = ScrollingMovementMethod();

    }

    override fun onResume() {
        super.onResume()
        root?.requestLayout()
    }
}
