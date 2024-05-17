package unitn.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.test.R
import com.squareup.picasso.Picasso
import unitn.app.api.Media
import unitn.app.localdb.Converters

class ViewHolderSearch {
    lateinit var poster: ImageView
    lateinit var title: TextView
}

class AdapterSearch(private var context: Context, private var media: List<Media>) :
    BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val itemInGrid: ViewHolderSearch
        var myView = convertView
        if (myView == null) {
            val mInflater = (context as Activity).layoutInflater
            myView = mInflater.inflate(R.layout.grid_item, parent, false)

            itemInGrid = ViewHolderSearch()
            itemInGrid.poster = myView!!.findViewById(R.id.imageView)!!
            itemInGrid.title = myView.findViewById(R.id.textView)!!

            myView.tag = itemInGrid
        } else {
            itemInGrid = myView.tag as ViewHolderSearch
        }

        if (media[position].posterPath != null) {
            showPoster(itemInGrid, media[position])
        }else {
            showTitle(itemInGrid, media[position])
        }

        myView.setOnClickListener{
            val intent = Intent(context, AggiungiMedia::class.java)
            prepareExtras(intent, media[position]);
            context.startActivity(intent)
        }
        return myView
    }

    private fun showTitle(itemInGrid: ViewHolderSearch, media: Media) {
        itemInGrid.poster.visibility = View.GONE
        itemInGrid.title.text = media.title
    }
    private fun showPoster(itemInGrid: ViewHolderSearch, media: Media) {
        itemInGrid.title.visibility = View.GONE
        Picasso.get().load(media.posterPath)
            .into(itemInGrid.poster);
    }
    private fun prepareExtras(intent: Intent, media: Media) {
        intent.putExtra("id", media.mediaId)
        intent.putExtra("titoloMedia", media.title)
        intent.putExtra("poster", media.posterPath)
        intent.putExtra("platforms", Converters().platformToString(media.platform))
        intent.putExtra("isFilm", media.isFilm)
        intent.putExtra("sinossi", media.sinossi)
    }

    override fun getItem(p0: Int): Any {
        return media[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return media.size
    }
}