package unitn.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.test.R
import kotlinx.coroutines.launch
import unitn.app.localdb.Converters
import unitn.app.localdb.MediaDatabase

class FilmFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_film, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaDao =
            Room.databaseBuilder(view.context, MediaDatabase::class.java, "media-DB")
                .addTypeConverter(Converters())
                .fallbackToDestructiveMigration()
                .build()
                .MediaDao()
        val gridViewFilm = view.findViewById<GridView>(R.id.GridViewFilm)

        lifecycleScope.launch {
            gridViewFilm.adapter = AdapterHomepage(view.context, mediaDao.getAllMovies())
        }
    }


}