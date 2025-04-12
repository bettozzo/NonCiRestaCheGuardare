package unitn.app.activities.ratings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.test.R
import unitn.app.remotedb.CronologiaConRating
import unitn.app.remotedb.Users

class FragmentRecensioni(private val ratingsMedia: List<Pair<Users, CronologiaConRating>>) :
    Fragment() {
    private var root: View? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_recensioni, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inflater = LayoutInflater.from(view.context);
        val lista = root!!.findViewById<LinearLayout>(R.id.listaRecensioni);
        for ((user, mediaVisto) in ratingsMedia) {
            val viewItem = inflater.inflate(R.layout.item_recensioni_altri_utenti, lista, false);

            val ratingBar = viewItem.findViewById<RatingBar>(R.id.rating);
            val usernameView = viewItem.findViewById<TextView>(R.id.usernameRecensione);
            val recensioneView = viewItem.findViewById<TextView>(R.id.recensione);
            val dataVisioneView = viewItem.findViewById<TextView>(R.id.dataVisione);

            usernameView.text = user.userId;
            if (mediaVisto.rating != null && mediaVisto.maxRating != null) {
                ratingBar.rating = mediaVisto.rating;
                ratingBar.numStars = mediaVisto.maxRating.toInt();
            } else {
                val nonRecensitoView = viewItem.findViewById<TextView>(R.id.nonRecensito);
                nonRecensitoView.visibility = View.VISIBLE;
                ratingBar.visibility = View.GONE;
            }
            if (mediaVisto.recensione != null) {
                recensioneView.text = mediaVisto.recensione;
            } else {
                recensioneView.hint = "Nessuna recensione :(";
            }

            dataVisioneView.text = formatToShowDate(mediaVisto.dataVisione);
            lista.addView(viewItem);
        }
    }

    override fun onResume() {
        super.onResume()
        root?.requestLayout()
    }

    private fun formatToShowDate(dataDaFromattare: String): String {
        val data = dataDaFromattare.split("T")[0];
        return data.split("-").reversed().joinToString("/")
    }

}
