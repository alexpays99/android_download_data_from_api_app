package com.example.android_download_data_from_api.general

import android.app.DownloadManager

class Constants {
    companion object {
        val shared = Constants()
    }

    val BASE_URL = "https://api.pexels.com/v1/"
    val API_KEY = "563492ad6f91700001000001d4c52c2f2ee64235aca0df2e105d6534"
    val UPDATE_STATE_ACTION = "UPDATE_STATE"
    val DOWNLOAD_COMPLETE_ACTION = DownloadManager.ACTION_DOWNLOAD_COMPLETE
    val position = "position"
    val state = "state"
    val id = "id"
    val counter = "counter"
}