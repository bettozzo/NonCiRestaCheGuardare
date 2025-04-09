package unitn.app.activities.ratings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.test.R
import unitn.app.activities.LiveDatas
import unitn.app.activities.segnaComeVisto.SegnaComeVisto
import unitn.app.remotedb.CronologiaConRating

class FragmentRecensioneUser(private val cronologia: List<CronologiaConRating>) : Fragment() {
    private var root: View? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_recensioni_user, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inflater = LayoutInflater.from(view.context);
        val lista = root!!.findViewById<LinearLayout>(R.id.listaRecensioni);
        for (mediaVisto in cronologia) {
            val viewItem = inflater.inflate(R.layout.item_recensione, lista, false);

            val editButtonView = viewItem.findViewById<Button>(R.id.editButton);
            val ratingBar = viewItem.findViewById<RatingBar>(R.id.rating);
            val recensioneView = viewItem.findViewById<TextView>(R.id.recensione);
            val dataVisioneView = viewItem.findViewById<TextView>(R.id.dataVisione);

            if (mediaVisto.rating != null && mediaVisto.maxRating != null) {
                ratingBar.rating = mediaVisto.rating;
                ratingBar.numStars = mediaVisto.maxRating.toInt();
            } else {
                val nonRecensitoView = viewItem.findViewById<TextView>(R.id.nonRecensito);
                nonRecensitoView.visibility = View.VISIBLE;
                ratingBar.visibility = View.GONE;
            }
            recensioneView.text = mediaVisto.recensione;

            dataVisioneView.text = mediaVisto.dataVisione.split("-").reversed().joinToString("/");

            editButtonView.setOnClickListener {
                val intent = Intent(context, SegnaComeVisto::class.java)
                intent.putExtra("isNewRating", false);
                intent.putExtra("mediaID", mediaVisto.media.mediaID);
                intent.putExtra("titoloFilm", mediaVisto.media.titolo);
                intent.putExtra("dataVisione", mediaVisto.dataVisione);
                intent.putExtra("rating", mediaVisto.rating);
                intent.putExtra("maxRating", mediaVisto.maxRating);
                intent.putExtra("recensione", mediaVisto.recensione);
                startActivity(intent);
            }

            LiveDatas.updateColorsOfButtons(listOf(editButtonView))
            lista.addView(viewItem);
        }
    }

    override fun onResume() {
        super.onResume()
        root?.requestLayout()
    }
}
