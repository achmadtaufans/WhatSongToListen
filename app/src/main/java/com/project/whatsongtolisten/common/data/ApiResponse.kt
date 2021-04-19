package com.project.whatsongtolisten.common.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("resultCount")
    @Expose
    var resultCount: Int,
    @SerializedName("results")
    @Expose
    var results: List<Song>? = null
)