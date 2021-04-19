package com.project.whatsongtolisten.common.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Song(
    @SerializedName("artistName")
    @Expose
    var artistName: String?,
    @SerializedName("trackName")
    @Expose
    var trackName: String?,
    @SerializedName("collectionName")
    @Expose
    var collectionName: String?,
    @SerializedName("previewUrl")
    @Expose
    var previewUrl: String?,
    @SerializedName("artworkUrl100")
    @Expose
    var artworkUrl100: String?,
    @SerializedName("releaseDate")
    @Expose
    var releaseDate: String?,
    @SerializedName("trackTimeMillis")
    @Expose
    var trackTimeMillis: Int?
)