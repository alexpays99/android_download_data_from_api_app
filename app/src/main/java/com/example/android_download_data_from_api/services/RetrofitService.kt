package com.example.android_download_data_from_api.services

import com.example.android_download_data_from_api.BuildConfig
import com.example.android_download_data_from_api.general.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {
    private var retrofit: Retrofit? = null

    companion object {
        private var sInstance: RetrofitService? = null

        fun getInstance(): RetrofitService {
            if (sInstance == null) sInstance = RetrofitService()
            return sInstance ?: throw IllegalStateException("")
        }
    }


    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient().newBuilder()

            // Logging for each request
            val logginInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                logginInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            }

            client.addInterceptor(Interceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("Authorization", Constants.API_KEY).build()
                )
            })

            retrofit = Retrofit.Builder()
                .client(client.build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit!!
    }
}