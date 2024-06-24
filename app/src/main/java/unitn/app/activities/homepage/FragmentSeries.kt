package unitn.app.activities.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.test.R
import unitn.app.activities.LiveDatas

class FragmentSeries : Fragment() {

    private var root: View? = null;
    private var dataLoaded = false;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_series, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!dataLoaded) {
            LiveDatas.liveWatchlist.observe(viewLifecycleOwner) {
                val gridViewMedia = view.findViewById<GridView>(R.id.GridViewSeries)
                val series = it.filter { !it.isFilm };
                gridViewMedia.adapter = AdapterHomepage(view.context, series)
                if (series.isNotEmpty()) {
                    view.findViewById<TextView>(R.id.isEmptyText).visibility = View.GONE;
                } else {
                    view.findViewById<TextView>(R.id.isEmptyText).visibility = View.VISIBLE;
                }
            }
            dataLoaded = true;
        }
    }
}