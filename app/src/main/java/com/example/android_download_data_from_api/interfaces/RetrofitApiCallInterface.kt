package com.example.android_download_data_from_api.interfaces

import com.example.android_download_data_from_api.models.User
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitApiCallInterface {
    @Headers("Authorization: 563492ad6f917000010000013a82c5e17bd7414699872efae15a20a8")
    @GET("search")
    fun getUsers(@Query(value = "query", encoded = true) query: String): Call<User>
}