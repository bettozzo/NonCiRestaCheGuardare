package unitn.app.api

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import unitn.app.localdb.Converters
import unitn.app.localdb.MediaDatabase
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MediaDetails(application: Application) : AndroidViewModel(application) {

    private var listMedia = emptyList<Media>().toMutableList()
    private val mutLiveListMedia = MutableLiveData<List<Media>>();
    private lateinit var currentMediaBeingQueried: String;
    val liveListMedia: LiveData<List<Media>>
        get() = mutLiveListMedia;

    suspend fun getDetails(mediaTitle: String, apiKey: String): Boolean =
        withContext(Dispatchers.IO) {
            currentMediaBeingQueried = mediaTitle;
            listMedia = emptyList<Media>().toMutableList()

            var counter = 0;
            val idFilmInUserList = getAllUserMedia();

            val apiCallerMedia = Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(RetrofitAPI::class.java)

            val movieSearchCall = apiCallerMedia.getMovie(mediaTitle, false, "it", 1, apiKey)
            val seriesSearchCall = apiCallerMedia.getSerie(mediaTitle, false, "it", 1, apiKey)

            val results = getMediaDetails(movieSearchCall).map { Pair(it, true) }.toMutableList()
            results.addAll(getMediaDetails(seriesSearchCall).map { Pair(it, false) })
            results.sortBy { it.first.popularity }
            results.reverse()

            if (results.isEmpty()) {
                if (currentMediaBeingQueried == mediaTitle) {
                    mutLiveListMedia.postValue(listMedia);
                }
                return@withContext false;
            }

            for ((media, isFilm) in results) {
                val id = media.id
                if (!idFilmInUserList.contains(id)) {
                    counter++;
                    val title = if (isFilm) {
                        media.title
                    } else {
                        media.name
                    }
                    val sinossi = media.overview
                    val platforms = getMediaPlatform(id, isFilm, apiKey)
                    val poster = getPosterPath(media.poster_path, media.backdrop_path)
                    val movie = Media(id, isFilm, title, platforms, poster, false, sinossi)

                    //prevents concurrency problems. In case user sends a new request before the previous one is finished
                    if (currentMediaBeingQueried == mediaTitle) {
                        listMedia.add(movie)
                        if (counter % 2 == 0) {
                            mutLiveListMedia.postValue(listMedia);
                        }
                    }
                }
            }

            if (currentMediaBeingQueried == mediaTitle) {
                mutLiveListMedia.postValue(listMedia);
            }
            return@withContext true;
        }

    suspend fun updateMediaList() {
        if (listMedia.isEmpty()) {
            return
        }

        val idFilmInUserList = getAllUserMedia();
        for (id in idFilmInUserList) {
            listMedia.removeIf { it.mediaId == id }
        }
        mutLiveListMedia.postValue(listMedia);
    }

    private suspend fun getAllUserMedia(): List<Int> {
        val context = getApplication<Application>().applicationContext
        val movieDao = Room.databaseBuilder(
            context,
            MediaDatabase::class.java, "database-name"
        ).addTypeConverter(Converters())
            .fallbackToDestructiveMigration()
            .build().MediaDao()

        return movieDao.getAllId()
    }
}

private fun getPosterPath(posterPath: String?, backdropPath: String?): String? {
    if (posterPath != null) {
        return "https://image.tmdb.org/t/p/w300$posterPath"
    }
    if (backdropPath != null) {
        return "https://image.tmdb.org/t/p/w300$backdropPath"
    }
    return null
}

suspend fun getMediaDetails(mediaSearchCall: Call<MediaResultsFromAPI?>?): MutableList<UnfilteredMediaDetails> {

    return suspendCoroutine { continuation ->
        mediaSearchCall?.enqueue(object : Callback<MediaResultsFromAPI?> {
            override fun onResponse(
                call: Call<MediaResultsFromAPI?>,
                response: Response<MediaResultsFromAPI?>,
            ) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                continuation.resume(response.body()!!.results.toMutableList())
            }

            override fun onFailure(call: Call<MediaResultsFromAPI?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}


suspend fun getMediaPlatform(
    id: Int,
    isFilm: Boolean,
    apiKey: String,
): MutableList<Pair<String, String>> {
    val platforms = mutableListOf<Pair<String, String>>()
    val mBaseUrl = if (isFilm) {
        "https://api.themoviedb.org/3/movie/$id/watch/"
    } else {
        "https://api.themoviedb.org/3/tv/$id/watch/"
    }
    val apiCallerPlatforms =
        Retrofit.Builder().baseUrl(mBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPI::class.java)

    return suspendCoroutine { continuation ->
        apiCallerPlatforms.getPlatform(apiKey)?.enqueue(object : Callback<StreamingResult?> {
            override fun onResponse(
                call: Call<StreamingResult?>,
                response: Response<StreamingResult?>,
            ) {

                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val optionsInItaly = response.body()!!.results.IT
                if (optionsInItaly == null) {
                    continuation.resume(emptyList<Pair<String, String>>().toMutableList())
                    return;
                }

                val flatrate = optionsInItaly.flatrate
                if (flatrate == null) {
                    continuation.resume(emptyList<Pair<String, String>>().toMutableList())
                    return;
                }
                for (platform in flatrate) {
                    platforms.add(
                        Pair(
                            platform.provider_name,
                            "https://image.tmdb.org/t/p/w185/" + platform.logo_path
                        )
                    )
                }
                continuation.resume(platforms)
            }

            override fun onFailure(call: Call<StreamingResult?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}