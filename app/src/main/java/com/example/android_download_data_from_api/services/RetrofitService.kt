package com.example.android_download_data_from_api.services

import com.example.android_download_data_from_api.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient().newBuilder()

            // Logging for each request
            val logginInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                logginInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            }
            client.addInterceptor(logginInterceptor)

            retrofit = Retrofit.Builder()
                .client(client.build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit!!
    }
}