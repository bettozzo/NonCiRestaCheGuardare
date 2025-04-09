package unitn.app.activities.feed

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.runBlocking
import unitn.app.activities.LiveDatas
import unitn.app.activities.aggiungiMedia.AggiungiMedia
import unitn.app.activities.ratings.DettaglioMedia
import unitn.app.remotedb.CronologiaMedia
import unitn.app.remotedb.RemoteDAO
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit


class Feed : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feed)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val homepageButton = findViewById<Button>(R.id.goToHomePage);
        homepageButton.setOnClickListener {
            finish();
        }
        LiveDatas.updateColorsOfButtons(listOf(homepageButton));

        val feed = mutableListOf<Pair<String, CronologiaMedia>>();
        runBlocking {
            val remoteDao = RemoteDAO(this@Feed, coroutineContext);
            val watchList = remoteDao.getEveryWatchlist(50).map { Pair("watchlist", it) };
            val cronologia = remoteDao.getEveryCronologia(50).map { Pair("cronologia", it) };
            val sortedFeed = (watchList + cronologia)
                .sortedByDescending { it.second.dataVisione }
                .take(50);
            feed.addAll(sortedFeed);
        }

        val feedView = findViewById<LinearLayout>(R.id.main);
        val inflater = LayoutInflater.from(this@Feed);
        var lastTimePeriodSeparator = "";
        for ((origine, mediaConInfo) in feed) {
            if (lastTimePeriodSeparator != getTimeAgo(mediaConInfo.dataVisione)) {
                lastTimePeriodSeparator = getTimeAgo(mediaConInfo.dataVisione);
                val separatoreView =
                    inflater.inflate(R.layout.item_soft_separatore_data, feedView, false);
                separatoreView.findViewById<TextView>(R.id.data).text = lastTimePeriodSeparator;
                feedView.addView(separatoreView);
            }
            val viewItem: View;
            if (origine == "watchlist") {
                viewItem = inflater.inflate(R.layout.item_feed_added_media, feedView, false);
                createItemAggiunto(
                    viewItem,
                    mediaConInfo.userid.userId,
                    mediaConInfo.mediaId.titolo,
                    mediaConInfo.mediaId.poster_path
                );
                viewItem.setOnClickListener {
                    val intent =
                        Intent(this, AggiungiMedia::class.java)
                    intent.putExtra("id", mediaConInfo.mediaId.mediaID);
                    intent.putExtra("titoloMedia", mediaConInfo.mediaId.titolo);
                    intent.putExtra("poster", mediaConInfo.mediaId.poster_path);
                    intent.putExtra("isFilm", mediaConInfo.mediaId.is_film);
                    intent.putExtra("sinossi", mediaConInfo.mediaId.sinossi);
                    intent.putExtra("annoUscita", mediaConInfo.mediaId.annoUscita);
                    intent.putExtra("generi", mediaConInfo.mediaId.generi);
                    intent.putExtra("durata", mediaConInfo.mediaId.durata);
                    startActivity(intent)
                }
            } else {
                viewItem = inflater.inflate(R.layout.item_feed_visto_media, feedView, false);
                createItemVisto(
                    viewItem,
                    mediaConInfo.userid.userId,
                    mediaConInfo.mediaId.titolo,
                    mediaConInfo.mediaId.poster_path,
                    mediaConInfo.rating,
                    mediaConInfo.maxRating
                );

                viewItem.setOnClickListener {
                    val intent =
                        Intent(this, DettaglioMedia::class.java)
                    intent.putExtra("mediaId", mediaConInfo.mediaId.mediaID)
                    startActivity(intent)
                }
            }
            feedView.addView(viewItem);
        }
    }

    private fun createItemAggiunto(
        viewItem: View,
        userId: String,
        titolo: String,
        posterPath: String?,
    ) {

        val usernameView = viewItem.findViewById<TextView>(R.id.info);
        val posterView = viewItem.findViewById<ImageView>(R.id.poster);
//            val actionImage = viewItem.findViewById<ImageView>(R.id.actionImage);

        val infoBuilder = SpannableStringBuilder();
        infoBuilder.bold { append(userId) }
            .append("\nha aggiunto\n")
            .append(titolo);
        usernameView.text = infoBuilder;

        Picasso.get().load(posterPath).placeholder(R.drawable.missing_poster)
            .into(posterView)


        viewItem.setBackgroundColor(ContextCompat.getColor(this, R.color.Grigio));
        viewItem.background.alpha = (0.38 * 255).toInt();
    }

    private fun createItemVisto(
        viewItem: View,
        userId: String,
        titolo: String,
        posterPath: String?,
        rating: Float?,
        maxRating: Float?,
    ) {

        val usernameView = viewItem.findViewById<TextView>(R.id.info);
        val posterView = viewItem.findViewById<ImageView>(R.id.poster);
        val ratingBar = viewItem.findViewById<RatingBar>(R.id.rating);
//            val actionImage = viewItem.findViewById<ImageView>(R.id.actionImage);

        val infoBuilder = SpannableStringBuilder();
        infoBuilder.bold { append(userId) }
            .append("\nha visto\n")
            .append(titolo);
        usernameView.text = infoBuilder;

        Picasso.get().load(posterPath).placeholder(R.drawable.missing_poster)
            .into(posterView)

//            Picasso.get()
//                .load(if (origine == "watchlist") R.drawable.cross else R.drawable.checkmark)
//                .placeholder(R.drawable.settings)
//                .into(actionImage)
//            if (origine == "watchlist") {
//                actionImage.rotation = 45F
//            }

        viewItem.setBackgroundColor(ContextCompat.getColor(this, R.color.Verde));
        viewItem.background.alpha = (0.4 * 255).toInt();

        if (rating == null) {
            ratingBar.visibility = View.GONE;
        } else if (maxRating != null) {
            ratingBar.rating = rating;
            ratingBar.numStars = maxRating.toInt();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTimeAgo(newDate: String): String {
        val currentCalendar = Calendar.getInstance()

        val targetCalendar = Calendar.getInstance();
        val timeParsed = SimpleDateFormat("yyyy-MM-dd").parse(newDate);
        if (timeParsed == null) {
            return "Boh tempo fa";
        } else {
            targetCalendar.time = timeParsed
        }

        val diffTs = currentCalendar.timeInMillis - targetCalendar.timeInMillis;
        val diffDays = (TimeUnit.MILLISECONDS.toDays(diffTs)).toInt();
        val diffWeeks = (TimeUnit.MILLISECONDS.toDays(diffTs) / 7).toInt();
        val diffMonths = (TimeUnit.MILLISECONDS.toDays(diffTs) / 30).toInt();

        return when {
            diffMonths == 1 -> "Un mese fa";
            diffMonths > 1 -> "$diffMonths mesi fa";
            diffWeeks == 1 -> "Una settimana fa";
            diffWeeks == 2 -> "Due settimana fa";
            diffWeeks == 3 -> "Tre settimana fa";
            diffWeeks == 4 -> "Quattro settimana fa";
            diffDays == 1 -> "Un giorno fa";
            diffDays == 2 -> "Due giorni fa";
            diffDays == 3 -> "Tre giorni fa";
            diffDays == 4 -> "Quattro giorni fa";
            diffDays == 5 -> "Cinque giorni fa";
            diffDays == 6 -> "Sei giorni fa";
            else -> "Oggi";
        }
    }
}
