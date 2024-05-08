package com.example.NonCiRestaCheGuardare.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class MediaDetails {
    fun getDetails(filmTitle: String): List<Movies> {
        val apiKey = "f256ec040f1c2b91ad903cc394728e55"

        val listMovies = mutableListOf<Movies>()
        getFullMovie(filmTitle, apiKey, listMovies)

        return listMovies
    }
}

fun main() {
    val listMovies = MediaDetails().getDetails("Kill Bill")
    println("Printing movies...")
    for (movie in listMovies) {
        println(movie.toString())
    }
    println("Printed movies")
}

fun getFullMovie(query: String, apiKey: String, listMovies: MutableList<Movies>) {

    val apiCallerMovies = Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitAPI::class.java)


    val movieSearchCall: Call<MovieResult?>? =
        apiCallerMovies.getMovie(query, false, "it", 1, apiKey)
    val results = getMoviesDetails(movieSearchCall)

    for (result in results) {
        val id = result.id
        val title = result.title
        val platforms = getMoviesPlatform(id, apiKey)

        val movie = Movies(id, title, platforms)
        listMovies.add(movie)
    }
}


fun getMoviesDetails(movieSearchCall: Call<MovieResult?>?): List<QueriedMovie> {
    val response = movieSearchCall?.execute()
    if (response == null) {
        System.err.println("Response to MovieDetails is NULL")
        return emptyList()
    }
    if (!response.isSuccessful) throw IOException("Unexpected code $response")
    return response.body()!!.results
}


fun getMoviesPlatform(id: Int, apiKey: String): MutableList<String> {
    val platforms = mutableListOf<String>()

    val apiCallerPlatforms =
        Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/movie/$id/watch/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPI::class.java)
    val response = apiCallerPlatforms.getPlatform(apiKey)?.execute()

    if (response == null) {
        System.err.println("Response to MovieDetails is NULL")
        return emptyList<String>().toMutableList()
    }

    if (!response.isSuccessful) throw IOException("Unexpected code $response")

    val optionsInItaly = response.body()!!.results.IT ?: return emptyList<String>().toMutableList()
    val flatrate = optionsInItaly.flatrate ?: return emptyList<String>().toMutableList()

    for (platform in flatrate) {
        platforms.add(platform.provider_name)
    }

    return platforms
}

