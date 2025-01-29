package unitn.app.api

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import unitn.app.activities.LiveDatas
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min


class MediaDetails(application: Application) : AndroidViewModel(application) {

    private var listMedia = emptyList<LocalMedia>().toMutableList()
    private var currentMediaBeingQueried: String = "";

    private var mutNoInternet: MutableLiveData<Boolean> = MutableLiveData(false)
    val liveNoInternet: LiveData<Boolean>
        get() = mutNoInternet;


    suspend fun getDetails(mediaTitle: String, apiKey: String, context: Context): Boolean =
        withContext(Dispatchers.IO) {

            //prevents concurrency problems. In case user sends a new request before the previous one is finished
            while (currentMediaBeingQueried != "") {
                Thread.sleep(100)
            }
            currentMediaBeingQueried = mediaTitle;
            listMedia = emptyList<LocalMedia>().toMutableList()

            LiveDatas.emptyRicercaMedia();
            val idFilmInUserList = getAllUserMedia();
            val results = getResults(mediaTitle, apiKey).filter { (resMedia, _) ->
                !idFilmInUserList.contains(resMedia!!.id)
            };

            if (results.isEmpty()) {
                currentMediaBeingQueried = "";
                return@withContext false;
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Trovati " + results.size + " risultati.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            //lavora in parallelo su diversi media ottenuri dalla chiamata API.
            //Suddivide in totale in numeri più piccoli di lavoro in modo da mostrarli più velocemente all'utente
            val listJobsMedia = emptyList<Job>().toMutableList();
            val size = results.size;
            var incrementSize = 5;
            var index = 0;
            val tmpListMedia = emptyList<LocalMedia?>().toMutableList()
            while (index < size) {
                tmpListMedia.clear();
                listJobsMedia.clear();
                for (i in index..min(index + incrementSize, size - 1)) {
                    val (resMedia, resIsFilm) = results[i];
                    tmpListMedia.add(null);
                    val job = launch {
                        val media = prepareResults(resMedia!!, resIsFilm!!, apiKey);
                            tmpListMedia[i - index] = media;
                    }
                    listJobsMedia.add(job)
                }
                listJobsMedia.joinAll();
                index += incrementSize + 1;
                incrementSize += 10;

                for (media in tmpListMedia) {
                    listMedia.add(media!!);
                    LiveDatas.addRicercaMedia(media);
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Finito!!", Toast.LENGTH_SHORT).show()
            }

            currentMediaBeingQueried = "";
            return@withContext true;
        }

    private suspend fun prepareResults(
        media: UnfilteredMediaDetails,
        isFilm: Boolean,
        apiKey: String,
    ): LocalMedia {
        val id = media.id
        val sinossi = media.overview
        val platforms = getMediaPlatform(id, isFilm, apiKey)
        val poster = getPosterPath(media.poster_path, media.backdrop_path)
        val (cast, crew) = getCredits(id, isFilm, apiKey);
        val (durata, annoUscita) = getDurataEAnnoUscita(id, isFilm, apiKey);
        val title = if (isFilm) {
            media.title!!
        } else {
            media.name!!
        }


        val generi = Genres.getGenres(media.genre_ids)

        val movie = LocalMedia(
            id,
            isFilm,
            title,
            platforms,
            poster,
            false,
            sinossi,
            annoUscita,
            generi,
            durata,
            cast,
            crew,
        )
        return movie;
    }


    private fun getResults(
        mediaTitle: String,
        apiKey: String,
    ): MutableList<Pair<UnfilteredMediaDetails?, Boolean?>> {
        val apiCallerMedia = Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(RetrofitAPI::class.java)

        var multiCall = apiCallerMedia.getMovieAndSeries(mediaTitle, false, "it", 1, apiKey)

        return runBlocking {
            val (results, totalPages) = getMediaDetails(multiCall)

            //pagina 1 già processata alla prima chiamata. Da quella ottengo il numero di pagine totali.
            for (pag in 2..min(totalPages, 4)) {
                multiCall = apiCallerMedia.getMovieAndSeries(mediaTitle, false, "it", pag, apiKey)
                results.addAll(getMediaDetails(multiCall).first)
            }
            val filteredResults = results.map { resultMedia ->
                if (LiveDatas.liveWatchlist.value?.none { liveResult -> liveResult.mediaId == resultMedia.first.id }!!) {
                    return@map resultMedia;
                } else {
                    return@map Pair(null, null);
                }
            }.toMutableList()
            filteredResults.removeAll { it.first == null }
            return@runBlocking filteredResults;
        }
    }

    private suspend fun getDurataEAnnoUscita(
        id: Int,
        isFilm: Boolean,
        apiKey: String,
    ): Pair<String, String> {
        val mBaseUrl = if (isFilm) {
            "https://api.themoviedb.org/3/movie/"
        } else {
            "https://api.themoviedb.org/3/tv/"
        }
        val apiCallerPlatforms =
            Retrofit.Builder().baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitAPI::class.java)


        if (isFilm) {
            return suspendCoroutine { continuation ->
                apiCallerPlatforms.getDetailsOnMovies(id, "it", apiKey)
                    ?.enqueue(object : Callback<ResDetailsMovie?> {
                        override fun onResponse(
                            call: Call<ResDetailsMovie?>,
                            response: Response<ResDetailsMovie?>,
                        ) {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            val res = response.body()!!;
                            val durata = res.runtime.toString();
                            val annoUscita = res.release_date.toString().split("-")[0];
                            continuation.resume(Pair(durata, annoUscita))
                        }

                        override fun onFailure(call: Call<ResDetailsMovie?>, t: Throwable) {
                            Log.d("Errore API movieDetailCall", t.toString())
                            failureNoInternet();
                        }
                    })
            }
        } else {
            return suspendCoroutine { continuation ->
                apiCallerPlatforms.getDetailsOnTvSeries(id, "it", apiKey)
                    ?.enqueue(object : Callback<ResDetailsTvSeries?> {
                        override fun onResponse(
                            call: Call<ResDetailsTvSeries?>,
                            response: Response<ResDetailsTvSeries?>,
                        ) {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            val res = response.body()!!;

                            Log.d("debugg", res.toString())
                            val durata =
                                res.number_of_seasons.toString() + " - " + res.number_of_episodes.toString();

                            val firstDate = res.first_air_date;
                            val lastDate = res.last_air_date;

                            if (firstDate == null || lastDate == null) {
                                continuation.resume(Pair(durata, ""))
                            } else {
                                val periodoUsicta = StringBuilder();
                                periodoUsicta.append(firstDate.split("-")[0])
                                periodoUsicta.append(" - ")

                                if (res.status == "Ended") {
                                    periodoUsicta.append(lastDate.split("-")[0])
                                } else {
                                    periodoUsicta.append("?")
                                }
                                continuation.resume(Pair(durata, periodoUsicta.toString()))
                            }
                        }

                        override fun onFailure(call: Call<ResDetailsTvSeries?>, t: Throwable) {
                            Log.d("Errore API serieDetailCall", t.toString())
                            failureNoInternet();
                        }
                    })
            }
        }
    }

    private suspend fun getCredits(
        id: Int,
        isFilm: Boolean,
        apiKey: String,
    ): Pair<List<Pair<String, String>>, List<Pair<String, String>>> {

        val mBaseUrl = if (isFilm) {
            "https://api.themoviedb.org/3/movie/"
        } else {
            "https://api.themoviedb.org/3/tv/"
        }
        val apiCallerPlatforms =
            Retrofit.Builder().baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitAPI::class.java)

        return suspendCoroutine { continuation ->
            apiCallerPlatforms.getCredits(id, apiKey)?.enqueue(object : Callback<CreditsResults?> {
                override fun onResponse(
                    call: Call<CreditsResults?>,
                    response: Response<CreditsResults?>,
                ) {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val castPopolarity = emptyList<Pair<String, String>>().toMutableList();
                    val crewPopolarity = emptyList<Triple<String, String, Float>>().toMutableList();
                    for (c in response.body()!!.cast) {
                        castPopolarity.add(Pair(c.name, c.character))
                    }
                    for (c in response.body()!!.crew.filter { it.job == "Director" || it.job == "Executive Producer" }) {
                        crewPopolarity.add(Triple(c.name, c.job, c.popularity))
                    }
                    var cast = castPopolarity.toList()
                    var crew = crewPopolarity.sortedWith(compareBy { it.third }).asReversed()
                        .map { Pair(it.first, it.second) }
                    val amount = 5;
                    if (cast.size > amount) {
                        cast = cast.take(amount)
                    }
                    if (crew.size > amount) {
                        crew = crew.take(amount)
                    }
                    continuation.resume(Pair(cast, crew))
                }

                override fun onFailure(call: Call<CreditsResults?>, t: Throwable) {
                    Log.d("Errore API", t.toString())
                    failureNoInternet();
                }
            })
        }
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
                    Log.d("Errore API", t.toString())
                    failureNoInternet();
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
                    Log.d("Errore API", t.toString())
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

