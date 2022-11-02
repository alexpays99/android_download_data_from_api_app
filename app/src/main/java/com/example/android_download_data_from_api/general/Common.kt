package com.example.android_download_data_from_api.general

import com.example.android_download_data_from_api.interfaces.RetrofitApiCallInterface
import com.example.android_download_data_from_api.services.RetrofitService

object Common {
    val retrofitService: RetrofitApiCallInterface
        get() = RetrofitService.getInstance().getClient(Constants.BASE_URL).create(RetrofitApiCallInterface::class.java)
}