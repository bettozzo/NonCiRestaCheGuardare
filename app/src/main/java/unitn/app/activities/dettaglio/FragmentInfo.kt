package unitn.app.activities.dettaglio

import android.app.Dialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
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

        //sinossi
        val sinossiView = view.findViewById<TextView>(R.id.sinossiText)
        val sinossi = extras.getString("sinossi", "no sinossi");
        sinossiView.text = sinossi;
        sinossiView.movementMethod = ScrollingMovementMethod();
    }
}