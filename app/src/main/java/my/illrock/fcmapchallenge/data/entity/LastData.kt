package my.illrock.fcmapchallenge.data.entity

import com.squareup.moshi.JsonClass
import my.illrock.fcmapchallenge.data.database.lastdata.LastDataEntity

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
) {
    constructor(entity: LastDataEntity) : this(
        entity.objectId,
        entity.timestamp,
        entity.latitude,
        entity.longitude,
        entity.address,
        entity.driverName,
        entity.plate,
        entity.speed,
        entity.lastEngineOnTime
    )

    fun toDbEntity() = LastDataEntity(
        objectId,
        timestamp,
        latitude,
        longitude,
        address,
        driverName,
        plate,
        speed,
        lastEngineOnTime
    )
}