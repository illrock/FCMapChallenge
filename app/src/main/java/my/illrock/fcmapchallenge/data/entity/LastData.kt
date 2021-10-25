package my.illrock.fcmapchallenge.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LastData(
    val objectId: Long,
    val timestamp: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val driverName: String?,
    val plate: String,
    val speed: Long?,
    val lastEngineOnTime: String?,
)