package com.example.android_download_data_from_api.models

import com.google.gson.annotations.SerializedName

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