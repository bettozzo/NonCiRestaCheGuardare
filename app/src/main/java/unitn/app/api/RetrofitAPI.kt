package unitn.app.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitAPI {

    @GET("search/movie?")
    fun getMovie(
        @Query("query") title: String,
        @Query("include_adult") include_adult: Boolean,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("api_key") api_key: String,
    ): Call<MediaResultsFromAPI?>?

    @GET("search/tv?")
    fun getSerie(
        @Query("query") title: String,
        @Query("include_adult") include_adult: Boolean,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("api_key") api_key: String,
    ): Call<MediaResultsFromAPI?>?

    @GET("providers?")
    fun getPlatform(@Query("api_key") api_key: String): Call<StreamingResult?>?
}
