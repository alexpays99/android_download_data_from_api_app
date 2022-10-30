package com.example.android_download_data_from_api.models

import com.example.android_download_data_from_api.general.ItemStatus
import com.google.gson.annotations.SerializedName

data class Photo(
    val id: Long,
    val width: Long,
    val photographer: String?,
    @SerializedName("photographer_id")
    val photographerID: Long,
    @SerializedName("avg_color")
    val avgColor: String?,
    val src: Src,
    val alt: String?,
    var state: ItemStatus = ItemStatus.DEFAULT
)