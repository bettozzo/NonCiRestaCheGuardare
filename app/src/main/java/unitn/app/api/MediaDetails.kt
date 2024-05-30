package unitn.app.api

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import unitn.app.LiveDatas
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MediaDetails(application: Application) : AndroidViewModel(application) {

    private var listMedia = emptyList<LocalMedia>().toMutableList()
    private lateinit var currentMediaBeingQueried: String;

    private var mutNoInternet: MutableLiveData<Boolean> = MutableLiveData(false)

    val liveNoInternet: LiveData<Boolean>
        get() = mutNoInternet;


    suspend fun getDetails(mediaTitle: String, apiKey: String): Boolean =
        withContext(Dispatchers.IO) {
            currentMediaBeingQueried = mediaTitle;
            listMedia = emptyList<LocalMedia>().toMutableList()
            var counter = 0;
            val idFilmInUserList = getAllUserMedia();

            val apiCallerMedia = Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(RetrofitAPI::class.java)

            var multiCall = apiCallerMedia.getMovieAndSeries(mediaTitle, false, "it", 1, apiKey)

            val (results, totalPages) = getMediaDetails(multiCall)
            for (pag in 2..totalPages) {
                multiCall = apiCallerMedia.getMovieAndSeries(mediaTitle, false, "it", pag, apiKey)
                results.addAll(getMediaDetails(multiCall).first)
            }

            results.sortBy { it.first.popularity }
            results.reverse()

            LiveDatas.emptyRicercaMedia()
            if (results.isEmpty()) {
                return@withContext false;
            }

            for ((media, isFilm) in results) {
                val list = LiveDatas.liveWatchlist.value?.filter { it.mediaId == media.id }
                if (list?.isNotEmpty()!!) {
                    continue;
                }
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
                    val movie = LocalMedia(id, isFilm, title!!, platforms, poster, false, sinossi)
                    LiveDatas.addRicercaMedia(movie);
                    //prevents concurrency problems. In case user sends a new request before the previous one is finished
                    if (currentMediaBeingQueried == mediaTitle) {
                        listMedia.add(movie)
                    }
                }
            }

            return@withContext true;
        }

    fun updateMediaList() {
        if (listMedia.isEmpty()) {
            return
        }

        val idMediaInUserList = getAllUserMedia();
        for (id in idMediaInUserList) {
            listMedia.removeIf { it.mediaId == id }
        }
    }

    private fun getAllUserMedia(): List<Int> {
        return LiveDatas.liveWatchlist.value?.map { it.mediaId } ?: emptyList()
    }

    private suspend fun getMediaDetails(mediaSearchCall: Call<MediaResultsFromAPI?>?): Pair<MutableList<Pair<UnfilteredMediaDetails, Boolean>>, Int> {

        return suspendCoroutine { continuation ->
            mediaSearchCall?.enqueue(object : Callback<MediaResultsFromAPI?> {
                override fun onResponse(
                    call: Call<MediaResultsFromAPI?>,
                    response: Response<MediaResultsFromAPI?>,
                ) {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val body = response.body()!!
                    continuation.resume(Pair(
                        body.results.filter { it.media_type != "person" && it.media_type != "collection" }
                            .map { Pair(it, it.media_type == "movie") }.toMutableList(),
                        body.total_pages
                    )
                    )
                }

                override fun onFailure(call: Call<MediaResultsFromAPI?>, t: Throwable) {
                    failureNoInternet();
                }
            })
        }
    }


    private suspend fun getMediaPlatform(
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
                    failureNoInternet();
                }
            })
        }
    }

    private fun failureNoInternet() {
        mutNoInternet.value = true
    }
}


private fun getPosterPath(posterPath: String?, backdropPath: String?): String? {
    if (posterPath != null) {
        return "https://image.tmdb.org/t/p/w780$posterPath"
    }
    if (backdropPath != null) {
        return "https://image.tmdb.org/t/p/w780$backdropPath"
    }
    return null
}

