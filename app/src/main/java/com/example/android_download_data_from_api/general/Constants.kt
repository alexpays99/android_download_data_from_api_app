package com.example.android_download_data_from_api.general

import android.app.DownloadManager

object Constants {
    const val BASE_URL = "https://api.pexels.com/v1/"
    const val API_KEY = "563492ad6f91700001000001d4c52c2f2ee64235aca0df2e105d6534"
    const val UPDATE_STATE_ACTION = "UPDATE_STATE"
    const val DOWNLOAD_COMPLETE_ACTION = DownloadManager.ACTION_DOWNLOAD_COMPLETE
    const val position = "position"
    const val state = "state"
    const val photo = "photo"
    const val photoId = "photoId"
    const val counter = "counter"
}