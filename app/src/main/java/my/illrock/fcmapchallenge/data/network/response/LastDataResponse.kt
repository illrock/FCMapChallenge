package my.illrock.fcmapchallenge.data.network.response

import com.squareup.moshi.JsonClass
import my.illrock.fcmapchallenge.data.entity.LastData

@JsonClass(generateAdapter = true)
data class LastDataResponse(
    val items: List<LastData>
)