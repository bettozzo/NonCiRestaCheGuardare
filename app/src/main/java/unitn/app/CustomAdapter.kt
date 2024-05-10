package unitn.app

import android.app.Activity
import android.content.Context
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

class CustomAdapter(context: Context, movies: List<Movies>) :
    BaseAdapter() {

    var context = context
    var movies = movies


    //Auto Generated Method
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var myView = convertView
        var holder: ViewHolder


        if (myView == null) {

            //If our View is Null than we Inflater view using Layout Inflater

            val mInflater = (context as Activity).layoutInflater

            //Inflating our grid_item.
            myView = mInflater.inflate(R.layout.grid_item, parent, false)

            //Create Object of ViewHolder Class and set our View to it
            holder = ViewHolder()


            //Find view By Id For all our Widget taken in grid_item.
            holder.mImageView = myView!!.findViewById<ImageView>(R.id.imageView) as ImageView
            holder.mTextView = myView.findViewById<TextView>(R.id.textView) as TextView

            //Set A Tag to Identify our view.
            myView.setTag(holder)
        } else {
            //If Our View in not Null than Just get View using Tag and pass to holder Object.
            holder = myView.getTag() as ViewHolder
        }

        Log.d("PRINT", movies[position].posterPath)
        Picasso.get().load(movies[position].posterPath).into(holder.mImageView);
        holder.mTextView!!.text = movies[position].title
        holder.mTextView!!.ellipsize = TextUtils.TruncateAt.MARQUEE;
        holder.mTextView!!.marqueeRepeatLimit = -1;
        holder.mTextView!!.setSingleLine(true);
        holder.mTextView!!.setSelected(true);
        return myView
    }

    override fun getItem(p0: Int): Any {
        return movies.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return movies.size
    }


    //Create A class To hold over View like we taken in grid_item.xml
    class ViewHolder {
        var mImageView: ImageView? = null
        var mTextView: TextView? = null
    }

}