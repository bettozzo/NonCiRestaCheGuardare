package unitn.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
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
    var poster: ImageView? = null
    var title: TextView? = null
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

        setTitleProperties(itemInGrid, media[position])
        setPosterProperties(itemInGrid, media[position])
        return myView
    }
    private fun setTitleProperties(itemInGrid: ViewHolderSearch, media: Media) {
        itemInGrid.title!!.text = media.title
        itemInGrid.title!!.ellipsize = TextUtils.TruncateAt.MARQUEE;
        itemInGrid.title!!.marqueeRepeatLimit = -1;
        itemInGrid.title!!.setSingleLine(true);
        itemInGrid.title!!.setSelected(true);
    }
    private fun setPosterProperties(itemInGrid: ViewHolderSearch, media: Media) {
        Picasso.get().load(media.posterPath).placeholder(R.drawable.missing_poster)
            .into(itemInGrid.poster);

        itemInGrid.poster!!.setOnClickListener {
            val intent = Intent(context, AggiungiMedia::class.java)
            prepareExtras(intent, media);
            context.startActivity(intent)
        }
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