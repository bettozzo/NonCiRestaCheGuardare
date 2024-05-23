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
import unitn.app.api.LocalDbMedia

class ViewHolderHomepage {
    lateinit var poster: ImageView
    lateinit var title: TextView
}

class AdapterHomepage(private var context: Context, private var localDbMedia: List<LocalDbMedia>) :
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

        if (localDbMedia[position].posterPath != null) {
            showPoster(itemInGrid, localDbMedia[position])
        }else {
            showTitle(itemInGrid, localDbMedia[position])
        }

        myView.setOnClickListener{
            val intent = Intent(context, DettaglioMedia::class.java)
            prepareExtras(intent, localDbMedia[position]);
            context.startActivity(intent)
        }
        return myView
    }

    private fun showTitle(itemInGrid: ViewHolderHomepage, localDbMedia: LocalDbMedia) {
        itemInGrid.poster.visibility = View.GONE
        itemInGrid.title.text = localDbMedia.title
    }
    private fun showPoster(itemInGrid: ViewHolderHomepage, localDbMedia: LocalDbMedia) {
        itemInGrid.title.visibility = View.GONE
        Picasso.get().load(localDbMedia.posterPath).into(itemInGrid.poster)
    }

    private fun prepareExtras(intent: Intent, localDbMedia: LocalDbMedia) {
        intent.putExtra("id", localDbMedia.mediaId)
        intent.putExtra("titoloFilm", localDbMedia.title)
        intent.putExtra("poster", localDbMedia.posterPath)
        intent.putExtra("isInLocal", localDbMedia.isLocallySaved)
        intent.putExtra("sinossi", localDbMedia.sinossi)

        //also update in ./DettaglioMedia.kt
        for (platform in localDbMedia.platform) {
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
        return localDbMedia[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return localDbMedia.size
    }
}

