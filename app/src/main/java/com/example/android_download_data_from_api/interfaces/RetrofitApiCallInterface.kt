package com.example.android_download_data_from_api.interfaces

import com.example.android_download_data_from_api.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitApiCallInterface {
    @GET("search")
    fun getUsers(@Query(value = "query", encoded = true) query: String): Call<User>
}