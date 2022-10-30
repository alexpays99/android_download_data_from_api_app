package com.example.android_download_data_from_api.general

import com.example.android_download_data_from_api.interfaces.RetrofitApiCallInterface
import com.example.android_download_data_from_api.services.RetrofitService

object Common {
private val BASE_URL = "https://api.pexels.com/v1/"
    val retrofitService: RetrofitApiCallInterface
        get() = RetrofitService.getClient(BASE_URL).create(RetrofitApiCallInterface::class.java)
}