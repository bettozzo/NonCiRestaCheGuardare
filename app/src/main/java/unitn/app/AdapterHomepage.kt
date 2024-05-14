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
import unitn.app.api.Movies

class ViewHolderHomepage {
    var poster: ImageView? = null
    var title: TextView? = null
}

class AdapterHomepage(private var context: Context, private var movies: List<Movies>) :
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

        setTitleProperties(itemInGrid, movies[position])
        setPosterProperties(itemInGrid, movies[position])
        return myView
    }

    private fun setTitleProperties(itemInGrid: ViewHolderHomepage, movie: Movies) {
        itemInGrid.title!!.text = movie.title
        itemInGrid.title!!.ellipsize = TextUtils.TruncateAt.MARQUEE;
        itemInGrid.title!!.marqueeRepeatLimit = -1;
        itemInGrid.title!!.setSingleLine(true);
        itemInGrid.title!!.setSelected(true);
    }
    private fun setPosterProperties(itemInGrid: ViewHolderHomepage, movie: Movies) {
        Picasso.get().load(movie.posterPath).placeholder(R.drawable.missing_poster)
            .into(itemInGrid.poster);

        itemInGrid.poster!!.setOnClickListener {
            val intent = Intent(context, DettaglioFilm::class.java)
            prepareExtras(intent, movie);
            context.startActivity(intent)
        }
    }

    private fun prepareExtras(intent: Intent, movie: Movies) {
        intent.putExtra("id", movie.id)
        intent.putExtra("titoloFilm", movie.title)
        intent.putExtra("poster", movie.posterPath)

        for (platform in movie.platform) {
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
                //TODO no raiplay, infinity?
            }
        }
    }


    override fun getItem(p0: Int): Any {
        return movies[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return movies.size
    }
}

