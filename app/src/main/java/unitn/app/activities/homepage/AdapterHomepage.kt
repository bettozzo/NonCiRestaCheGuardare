package unitn.app.activities.homepage

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
import unitn.app.activities.dettaglio.DettaglioMedia
import unitn.app.api.LocalMedia

class ViewHolderHomepage {
    lateinit var poster: ImageView
    lateinit var title: TextView
}

class AdapterHomepage(private var context: Context, private var localMedia: List<LocalMedia>) :
    BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemInGrid: ViewHolderHomepage
        var myView = convertView
        if (myView == null) {
            val mInflater = (context as Activity).layoutInflater
            myView = mInflater.inflate(R.layout.item_grid, parent, false)
            itemInGrid = ViewHolderHomepage()
            itemInGrid.poster = myView!!.findViewById(R.id.imageView)!!
            itemInGrid.title = myView.findViewById(R.id.textView)!!
            myView.tag = itemInGrid
        } else {
            itemInGrid = myView.tag as ViewHolderHomepage
        }

        if (localMedia[position].posterPath != null) {
            showPoster(itemInGrid, localMedia[position])
        }else {
            showTitle(itemInGrid, localMedia[position])
        }

        myView.setOnClickListener{
            val intent = Intent(context, DettaglioMedia::class.java)
            prepareExtras(intent, localMedia[position]);
            context.startActivity(intent)
        }
        return myView
    }

    private fun showTitle(itemInGrid: ViewHolderHomepage, localMedia: LocalMedia) {
        itemInGrid.poster.visibility = View.GONE
        itemInGrid.title.text = localMedia.title
    }
    private fun showPoster(itemInGrid: ViewHolderHomepage, localMedia: LocalMedia) {
        itemInGrid.title.visibility = View.GONE
        Picasso.get().load(localMedia.posterPath).into(itemInGrid.poster)
    }

    private fun prepareExtras(intent: Intent, localMedia: LocalMedia) {
        intent.putExtra("id", localMedia.mediaId)
        intent.putExtra("titoloFilm", localMedia.title)
        intent.putExtra("poster", localMedia.posterPath)
        intent.putExtra("isInLocal", localMedia.isLocallySaved)
        intent.putExtra("sinossi", localMedia.sinossi)

        //also update in ./DettaglioMedia.kt
        for (platform in localMedia.platform) {
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
            }
        }
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

