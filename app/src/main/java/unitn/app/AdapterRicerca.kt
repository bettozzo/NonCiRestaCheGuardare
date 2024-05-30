package unitn.app

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
            myView = mInflater.inflate(R.layout.grid_item, parent, false)

            itemInGrid = ViewHolderSearch()
            itemInGrid.poster = myView!!.findViewById(R.id.imageView)!!
            itemInGrid.title = myView.findViewById(R.id.textView)!!
            myView.tag = itemInGrid
        } else {
            itemInGrid = myView.tag as ViewHolderSearch
        }

        if (localMedia[position].posterPath != null) {
            showPoster(itemInGrid, localMedia[position]);
        } else {
            showTitle(itemInGrid, localMedia[position]);
        }

        myView.setOnClickListener {
            val intent = Intent(context, AggiungiMedia::class.java)
            prepareExtras(intent, localMedia[position]);
            context.startActivity(intent)
        }
        return myView
    }

    private fun showTitle(itemInGrid: ViewHolderSearch, localMedia: LocalMedia) {
        itemInGrid.poster.visibility = View.GONE
        itemInGrid.title.text = localMedia.title
    }

    private fun showPoster(itemInGrid: ViewHolderSearch, localMedia: LocalMedia) {
        itemInGrid.title.visibility = View.GONE
        Picasso.get().load(localMedia.posterPath).into(itemInGrid.poster);
    }


    private fun prepareExtras(intent: Intent, localMedia: LocalMedia) {
        intent.putExtra("id", localMedia.mediaId)
        intent.putExtra("titoloMedia", localMedia.title)
        intent.putExtra("poster", localMedia.posterPath)
        intent.putExtra("platforms", Converters().platformToString(localMedia.platform))
        intent.putExtra("isFilm", localMedia.isFilm)
        intent.putExtra("sinossi", localMedia.sinossi)
        intent.putExtra("cast", localMedia.cast as ArrayList<String>)
        intent.putExtra("crew", localMedia.crew as ArrayList<String>)
    }

    override fun getItem(p0: Int): Any {
        return localMedia[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return localMedia.size
    }
}