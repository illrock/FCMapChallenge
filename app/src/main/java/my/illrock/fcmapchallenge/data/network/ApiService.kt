package my.illrock.fcmapchallenge.data.network

import my.illrock.fcmapchallenge.data.entity.LastData
import my.illrock.fcmapchallenge.data.entity.RawData
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("Vehicles/getLastData")
    suspend fun getLastData(
        @Query("key") key: String,
        @Query("json") isJson: Boolean = true
    ): List<LastData>

    @GET("Vehicles/getRawData")
    suspend fun getRawData(
        @Query("objectId") objectId: Long,
        //2020-10-30
        @Query("begTimestamp") begTimestamp: String,
        @Query("endTimestamp") endTimestamp: String,
        @Query("key") key: String,
        @Query("json") isJson: Boolean = true
    ): List<RawData>
}