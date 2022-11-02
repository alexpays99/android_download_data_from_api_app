package com.example.android_download_data_from_api.general

import java.io.Serializable

enum class ItemStatus: Serializable {
    DEFAULT,
    IN_PROGRESS,
    DOWNLOADED,
    IN_QUEUE
}