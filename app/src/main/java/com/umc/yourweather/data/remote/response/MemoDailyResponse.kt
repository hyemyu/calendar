package com.umc.yourweather.data.remote.response

import com.google.gson.annotations.SerializedName

data class MemoDailyResponse(
    @SerializedName("daily_memolist")
    val memoList: List<MemoItemResponse>,
    @SerializedName("daily_memocontentlist")
    val memoContentList: List<MemoContentResponse>,
){
    data class MemoItemResponse(
        @SerializedName("memoId")
        val memoId: Int,
        @SerializedName("creationDatetime")
        val dateTime: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("temperature")
        val temperature: Int,
    )
    enum class Status {
        @SerializedName("SUNNY")
        SUNNY,
        @SerializedName("CLOUDY")
        CLOUDY,
        @SerializedName("RAINY")
        RAINY,
        @SerializedName("LIGHTNING")
        LIGHTNING
    }

    data class MemoContentResponse(
        @SerializedName("memoId")
        val memoId: Int,
        @SerializedName("creationDatetime")
        val dateTime: String,
        @SerializedName("content")
        val status: String,
    )
}
