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
    private var scrolly = 0;
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

        val gridViewMedia = view.findViewById<GridView>(R.id.GridViewSeries)
        val series = LiveDatas.liveWatchlist.value!!.filter { !it.isFilm }.toMutableList()
        gridViewMedia.adapter = AdapterHomepage(view.context, series)

        if (series.isNotEmpty()) {
            view.findViewById<TextView>(R.id.isEmptyText).visibility = View.GONE;
        } else {
            view.findViewById<TextView>(R.id.isEmptyText).visibility = View.VISIBLE;
        }
    }


    override fun onPause() {
        super.onPause()
        val gridViewMedia = view?.findViewById<GridView>(R.id.GridViewSeries)!!
        scrolly = gridViewMedia.firstVisiblePosition
    }

    override fun onResume() {
        super.onResume()
        val gridViewMedia = view?.findViewById<GridView>(R.id.GridViewSeries)!!
        gridViewMedia.smoothScrollToPosition(scrolly)
        val series = LiveDatas.liveWatchlist.value!!.filter { !it.isFilm }
        (gridViewMedia.adapter as AdapterHomepage).customNotifyDataSetIsChanged(series);
    }
}