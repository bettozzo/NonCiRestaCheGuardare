package unitn.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.test.R
import com.squareup.picasso.Picasso
import unitn.app.api.Movies

class ViewHolder {
    var poster: ImageView? = null
    var title: TextView? = null
}

class CustomAdapter(private var context: Context, private var movies: List<Movies>) :
    BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val itemInGrid: ViewHolder
        var myView = convertView
        if (myView == null) {
            val mInflater = (context as Activity).layoutInflater
            myView = mInflater.inflate(R.layout.grid_item, parent, false)

            itemInGrid = ViewHolder()
            itemInGrid.poster = myView!!.findViewById(R.id.imageView)!!
            itemInGrid.title = myView.findViewById(R.id.textView)!!

            myView.tag = itemInGrid
        } else {
            itemInGrid = myView.tag as ViewHolder
        }

        //set poster image
        Picasso.get().load(movies[position].posterPath).placeholder(R.drawable.missing_poster)
            .into(itemInGrid.poster);

        //set title
        itemInGrid.title!!.text = movies[position].title
        itemInGrid.title!!.ellipsize = TextUtils.TruncateAt.MARQUEE;
        itemInGrid.title!!.marqueeRepeatLimit = -1;
        itemInGrid.title!!.setSingleLine(true);
        itemInGrid.title!!.setSelected(true);

        itemInGrid.poster!!.setBackgroundColor(Color.parseColor("#f0f0f0"))
        itemInGrid.poster!!.setOnClickListener {
            val intent = Intent(context, DettaglioFilm::class.java)
            intent.putExtra("titoloFilm", movies[position].title)
            intent.putExtra("poster", movies[position].posterPath)

            for (platform in movies[position].platform) {
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
            context.startActivity(intent)
        }
        return myView
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