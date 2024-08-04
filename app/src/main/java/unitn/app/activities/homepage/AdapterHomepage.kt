package unitn.app.activities.homepage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.test.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unitn.app.activities.LiveDatas
import unitn.app.activities.dettaglio.DettaglioMedia
import unitn.app.api.LocalMedia
import unitn.app.remotedb.RemoteDAO

class ViewHolderHomepage {
    lateinit var poster: ImageView
    lateinit var title: TextView
}

class AdapterHomepage(
    private var context: Context,
    private var listMedia: MutableList<LocalMedia>,
) :
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

        val media = listMedia[position]
        if (media.posterPath != null) {
            showPoster(itemInGrid, media)
        } else {
            showTitle(itemInGrid, media)
        }

        myView.setOnClickListener {
            val intent = Intent(context, DettaglioMedia::class.java)
            prepareExtras(intent, media);
            context.startActivity(intent)
        }

        val toRemove = LiveDatas.getIdInListToRemove(media.isFilm) == position;
        if (toRemove) {
            setAnimation(media, myView)
        }

        return myView
    }

    private fun setAnimation(media: LocalMedia, myView: View) {
        val animationManager = Animations(context);
//        val animation = animationManager.moveOut();
        val animation = animationManager.getRandomAnimation();

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                myView.animation.cancel();
                LiveDatas.setIdToRemove(null);
                listMedia.remove(media);
                notifyDataSetChanged();
                CoroutineScope(Dispatchers.IO).launch {
                    val remoteDao = RemoteDAO(
                        context,
                        coroutineContext
                    );
                    remoteDao.deleteFromWatchList(media.mediaId, this@AdapterHomepage)
                }
            }
        })
        myView.startAnimation(animation)
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
        intent.putExtra("isFilm", localMedia.isFilm)

        //also update in ../dettaglio/DettaglioMedia.kt
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
        return listMedia[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return listMedia.size
    }

    fun customNotifyDataSetIsChanged(newListMedia: List<LocalMedia>){
        listMedia = newListMedia.toMutableList();
        notifyDataSetChanged();
    }

}

