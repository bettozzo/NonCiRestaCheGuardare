package unitn.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.test.R

class SeriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LiveDatas.liveWatchlist.observe(viewLifecycleOwner){
            val gridViewMedia = view.findViewById<GridView>(R.id.GridViewSeries)
            gridViewMedia.adapter = AdapterHomepage(view.context, it.filter { !it.isFilm })
        }
    }
}