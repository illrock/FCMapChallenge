package my.illrock.fcmapchallenge.data.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RawData(
    val timestamp: String,
    @Json(name = "Direction")
    val direction: String?,
    @Json(name = "Longitude")
    val longitude: Double?,
    @Json(name = "Latitude")
    val latitude: Double?,
)