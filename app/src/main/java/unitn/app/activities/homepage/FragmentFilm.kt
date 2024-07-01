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


class FragmentFilm : Fragment() {

    private var root: View? = null;
    private var scrolly = 0;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_film, container, false)
        }
        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridViewMedia = view.findViewById<GridView>(R.id.GridViewFilm)
        val movies = LiveDatas.liveWatchlist.value!!.filter { it.isFilm }.toMutableList()
        gridViewMedia.adapter = AdapterHomepage(view.context, movies);
        gridViewMedia.smoothScrollToPosition(scrolly)
        if (movies.isNotEmpty()) {
            view.findViewById<TextView>(R.id.isEmptyText).visibility = View.GONE;
        } else {
            view.findViewById<TextView>(R.id.isEmptyText).visibility = View.VISIBLE;
        }
    }

    override fun onPause() {
        super.onPause()
        val gridViewMedia = view?.findViewById<GridView>(R.id.GridViewFilm)!!
        scrolly = gridViewMedia.firstVisiblePosition
    }

    override fun onResume() {
        super.onResume()
        val gridViewMedia = view?.findViewById<GridView>(R.id.GridViewFilm)!!
        gridViewMedia.smoothScrollToPosition(scrolly)

        val movies = LiveDatas.liveWatchlist.value!!.filter { it.isFilm }
        (gridViewMedia.adapter as AdapterHomepage).customNotifyDataSetIsChanged(movies);
    }
}