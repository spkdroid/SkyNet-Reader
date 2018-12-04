package com.dija.skynet.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NewsData {

    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("link")
    @Expose
    var link: String? = null
    @SerializedName("temp")
    @Expose
    var temp: String? = null
    @SerializedName("date")
    @Expose
    var date: String? = null
}