package my.illrock.fcmapchallenge.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RawDataResponse(
    val timestamp: String,
    @Json(name = "Direction")
    val direction: String,
    @Json(name = "Longitude")
    val longitude: Double,
    @Json(name = "Latitude")
    val latitude: Double,
)