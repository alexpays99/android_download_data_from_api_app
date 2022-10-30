package com.example.android_download_data_from_api.interfaces

import com.example.android_download_data_from_api.models.Photo

interface OnBindInterface {
    fun onBinding(photo: Photo)
}

interface OnDownloadImageInterface {
    fun onDownloadImage(photo: Photo, position: Int)
}