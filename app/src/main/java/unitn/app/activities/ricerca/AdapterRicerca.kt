package unitn.app.activities.ricerca

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.test.R
import com.squareup.picasso.Picasso
import unitn.app.activities.aggiungiMedia.AggiungiMedia
import unitn.app.api.LocalMedia
import unitn.app.localdb.Converters

class ViewHolderSearch {
    lateinit var poster: ImageView
    lateinit var title: TextView
}

class AdapterSearch(private var context: Context, private var localMedia: List<LocalMedia>) :
    BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val itemInGrid: ViewHolderSearch
        var myView = convertView
        if (myView == null) {
            val mInflater = (context as Activity).layoutInflater
            myView = mInflater.inflate(R.layout.item_grid_media, parent, false)

            itemInGrid = ViewHolderSearch()
            itemInGrid.poster = myView!!.findViewById(R.id.imageView)!!
            itemInGrid.title = myView.findViewById(R.id.textView)!!
            myView.tag = itemInGrid
        } else {
            itemInGrid = myView.tag as ViewHolderSearch
        }

        if (localMedia[position].posterPath != null) {
            showPoster(itemInGrid, localMedia[position].posterPath!!);
        } else {
            showTitle(itemInGrid, localMedia[position].title);
        }

        myView.setOnClickListener {
            val intent = Intent(context, AggiungiMedia::class.java)
            prepareExtras(intent, localMedia[position]);
            context.startActivity(intent)
        }
        return myView
    }

    private fun showTitle(itemInGrid: ViewHolderSearch, title: String) {
        itemInGrid.poster.visibility = View.GONE
        itemInGrid.title.text = title
    }

    private fun showPoster(itemInGrid: ViewHolderSearch, posterPath: String) {
        itemInGrid.title.visibility = View.GONE
        Picasso.get().load(posterPath).into(itemInGrid.poster);
    }



    private fun prepareExtras(intent: Intent, localMedia: LocalMedia) {
        intent.putExtra("id", localMedia.mediaId)
        intent.putExtra("titoloMedia", localMedia.title)
        intent.putExtra("poster", localMedia.posterPath)
        intent.putExtra("platforms", Converters().platformToString(localMedia.platform))
        intent.putExtra("isFilm", localMedia.isFilm)
        intent.putExtra("sinossi", localMedia.sinossi)
        intent.putExtra("cast", Converters().platformToString(localMedia.cast))
        intent.putExtra("crew", Converters().platformToString(localMedia.crew))
        intent.putExtra("generi", localMedia.generi)
        intent.putExtra("annoUscita", localMedia.annoUscita)
        intent.putExtra("durata", localMedia.durata)
    }

    override fun getItem(p0: Int): Any {
        return localMedia[p0]
    }

    override fun getItemId(p0: Int): Long {
        return (p0/3).toLong()
    }

    override fun getCount(): Int {
        return localMedia.size
    }
}