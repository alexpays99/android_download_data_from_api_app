package com.example.android_download_data_from_api.models

import com.google.gson.annotations.SerializedName

data class User(
    val page: Long,
    @SerializedName("per_page")
    val perPage: Long,
    val photos: List<Photo>,
    @SerializedName("total_results")
    val totalResults: Long,
    @SerializedName("next_page")
    val nextPage: String
)
