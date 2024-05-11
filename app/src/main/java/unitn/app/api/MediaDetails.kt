package unitn.app.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MediaDetails {
    suspend fun getDetails(filmTitle: String, apiKey: String): List<Movies> = withContext(
        Dispatchers.IO) {

        val listMovies = mutableListOf<Movies>()
        getFullMovie(filmTitle, apiKey, listMovies)

        return@withContext listMovies
    }
}

fun main() = runBlocking {
    val listMovies = MediaDetails().getDetails("Kill Bill", "f256ec040f1c2b91ad903cc394728e55")
    println("Printing movies...")
    for (movie in listMovies) {
        println(movie.toString())
    }
    println("Printed movies")
}

suspend fun getFullMovie(query: String, apiKey: String, listMovies: MutableList<Movies>) {

    val apiCallerMovies = Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create()).build().create(RetrofitAPI::class.java)


    val movieSearchCall = apiCallerMovies.getMovie(query, false, "it", 1, apiKey)

    val results = getMoviesDetails(movieSearchCall)

    for (result in results) {
        val id = result.id
        val title = result.title
        val platforms = getMoviesPlatform(id, apiKey)
        val poster = getMoviePoster(id, apiKey) ?: "No image"


        val movie = Movies(id, title, platforms, poster)
        listMovies.add(movie)
    }
}


suspend fun getMoviesDetails(movieSearchCall: Call<MovieResult?>?): List<QueriedMovie> {

    return suspendCoroutine { continuation ->
        movieSearchCall?.enqueue(object : Callback<MovieResult?> {
            override fun onResponse(call: Call<MovieResult?>, response: Response<MovieResult?>) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                continuation.resume(response.body()!!.results)
            }

            override fun onFailure(call: Call<MovieResult?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}


suspend fun getMoviesPlatform(id: Int, apiKey: String): MutableList<Pair<String, String>> {
    val platforms = mutableListOf<Pair<String, String>>()

    val apiCallerPlatforms =
        Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/movie/$id/watch/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPI::class.java)

    return suspendCoroutine { continuation ->
        apiCallerPlatforms.getPlatform(apiKey)?.enqueue(object : Callback<StreamingResult?> {
            override fun onResponse(
                call: Call<StreamingResult?>,
                response: Response<StreamingResult?>
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
                    platforms.add(Pair(platform.provider_name, "https://image.tmdb.org/t/p/w185/"+platform.logo_path))
                }
                continuation.resume(platforms)
            }

            override fun onFailure(call: Call<StreamingResult?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}

suspend fun getMoviePoster(id: Int, apiKey: String): String? {
    val apiCallerPlatforms =
        Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/movie/$id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPI::class.java)

    return suspendCoroutine { continuation ->
        apiCallerPlatforms.getPoster(apiKey)?.enqueue(object : Callback<MovieResults?> {
            override fun onResponse(call: Call<MovieResults?>, response: Response<MovieResults?>) {
                if (!response.isSuccessful) throw IOException("Unexpected co de $response")

                val posters = response.body()!!.posters ?: emptyList();
                val backdrops = response.body()!!.backdrops ?: emptyList();
                val logo = response.body()!!.logo ?: emptyList();

                val resolution = "w300"

                if (posters.isNotEmpty()) {
                    continuation.resume(
                        "https://image.tmdb.org/t/p/$resolution" + filterImages(
                            posters
                        )
                    )
                    return;
                }
                if (logo.isNotEmpty()) {
                    continuation.resume("https://image.tmdb.org/t/p/$resolution" + filterImages(logo))
                    return;
                }
                if (backdrops.isNotEmpty()) {
                    continuation.resume(
                        "https://image.tmdb.org/t/p/$resolution" + filterImages(
                            backdrops
                        )
                    )
                    return;
                }
                continuation.resume(null)
                return;
            }

            override fun onFailure(call: Call<MovieResults?>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}

fun filterImages(listImages: List<MovieImageInfo>): String {
    var bestFilmSoFar = listImages[0].file_path

    for (image in listImages) {
        if (image.iso_639_1 == "it") {
            return image.file_path;
        }
        if (image.iso_639_1 == "en" && bestFilmSoFar == listImages[0].file_path) {
            bestFilmSoFar = image.file_path
        }
    }
    return bestFilmSoFar
}
