package unitn.app.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitAPI {

    @GET("search/multi?")
    fun getMovieAndSeries(
        @Query("query") title: String,
        @Query("include_adult") include_adult: Boolean,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("api_key") api_key: String,
    ): Call<MediaResultsFromAPI?>?


    @GET("{movie_id}?")
    fun getDetailsOnMovies(
        @Path("movie_id") movie_id: Int,
        @Query("language") language: String,
        @Query("api_key") api_key: String,
    ): Call<ResDetailsMovie?>?
    @GET("{series_id}?")
    fun getDetailsOnTvSeries(
        @Path("series_id") series_id: Int,
        @Query("language") language: String,
        @Query("api_key") api_key: String,
    ): Call<ResDetailsTvSeries?>?


    @GET("providers?")
    fun getPlatform(@Query("api_key") api_key: String): Call<StreamingResult?>?


    @GET("{id}/credits?")
    fun getCredits(@Path("id") path:Int, @Query("api_key") api_key: String): Call<CreditsResults?>?
}
