package my.illrock.fcmapchallenge.data.network

import io.reactivex.rxjava3.core.Single
import my.illrock.fcmapchallenge.data.entity.LastData
import my.illrock.fcmapchallenge.data.entity.RawData
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("Vehicles/getLastData")
    fun getLastData(
        @Query("key") key: String,
        @Query("json") isJson: Boolean = true
    ): Single<List<LastData>>

    @GET("Vehicles/getRawData")
    fun getRawData(
        @Query("objectId") objectId: Long,
        //2020-10-30
        @Query("begTimestamp") begTimestamp: String,
        @Query("endTimestamp") endTimestamp: String,
        @Query("key") key: String,
        @Query("json") isJson: Boolean = true
    ): Single<List<RawData>>
}