package com.dija.skynet.service

import com.dija.skynet.data.NewsData
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable

interface FetchNewsFeedAPI {

    @GET("canada.php")
    fun loadPredictions(@Query("type") newsTypeCode: String): Observable<ArrayList<NewsData>>

    companion object {

        private val BASE_URL = "http://www.spkdroid.com/News/"

        fun create(): FetchNewsFeedAPI {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(FetchNewsFeedAPI::class.java)
        }
    }
}