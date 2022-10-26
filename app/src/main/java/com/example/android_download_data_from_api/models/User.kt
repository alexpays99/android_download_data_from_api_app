package com.example.android_download_data_from_api.models

import com.google.gson.annotations.SerializedName

data class User (
    val page: Long,
    @SerializedName("per_page")
    val perPage: Long,
    val photos: List<Photo>,
    @SerializedName("total_results")
    val totalResults: Long,
    @SerializedName("next_page")
    val nextPage: String
)

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
)

data class Src (
    val original: String,
    @SerializedName("large2x")
    val large2X: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)
