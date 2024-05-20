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
import unitn.app.api.Media

class ViewHolderHomepage {
    lateinit var poster: ImageView
    lateinit var title: TextView
}

class AdapterHomepage(private var context: Context, private var media: List<Media>) :
    BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemInGrid: ViewHolderHomepage
        var myView = convertView
        if (myView == null) {
            val mInflater = (context as Activity).layoutInflater
            myView = mInflater.inflate(R.layout.grid_item, parent, false)
            itemInGrid = ViewHolderHomepage()
            itemInGrid.poster = myView!!.findViewById(R.id.imageView)!!
            itemInGrid.title = myView.findViewById(R.id.textView)!!
            myView.tag = itemInGrid
        } else {
            itemInGrid = myView.tag as ViewHolderHomepage
        }

        if (media[position].posterPath != null) {
            showPoster(itemInGrid, media[position])
        }else {
            showTitle(itemInGrid, media[position])
        }

        myView.setOnClickListener{
            val intent = Intent(context, DettaglioMedia::class.java)
            prepareExtras(intent, media[position]);
            context.startActivity(intent)
        }
        return myView
    }

    private fun showTitle(itemInGrid: ViewHolderHomepage, media: Media) {
        itemInGrid.poster.visibility = View.GONE
        itemInGrid.title.text = media.title
    }
    private fun showPoster(itemInGrid: ViewHolderHomepage, media: Media) {
        itemInGrid.title.visibility = View.GONE
        Picasso.get().load(media.posterPath).into(itemInGrid.poster)
    }

    private fun prepareExtras(intent: Intent, media: Media) {
        intent.putExtra("id", media.mediaId)
        intent.putExtra("titoloFilm", media.title)
        intent.putExtra("poster", media.posterPath)
        intent.putExtra("isInLocal", media.isLocallySaved)
        intent.putExtra("sinossi", media.sinossi)

        //also update in ./DettaglioMedia.kt
        for (platform in media.platform) {
            when (platform.first) {
                "Netflix" -> {
                    intent.putExtra("NetflixPath", platform.second)
                };
                "Amazon Prime Video" -> {
                    intent.putExtra("AmazonPath", platform.second)
                };
                "Disney Plus" -> {
                    intent.putExtra("DisneyPath", platform.second)
                };
                "Rai Play" -> {
                    intent.putExtra("RaiPath", platform.second)
                };
                "Crunchyroll" -> {
                    intent.putExtra("CrunchyrollPath", platform.second)
                };
                //TODO no , infinity?
            }
        }
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

