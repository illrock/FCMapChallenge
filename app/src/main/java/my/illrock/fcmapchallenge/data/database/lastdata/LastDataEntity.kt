package my.illrock.fcmapchallenge.data.database.lastdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import my.illrock.fcmapchallenge.data.database.lastdata.LastDataEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class LastDataEntity(
    @PrimaryKey
    @ColumnInfo(name = OBJECT_ID)
    val objectId: Long,
    @ColumnInfo(name = TIMESTAMP)
    val timestamp: String,
    @ColumnInfo(name = LATITUDE)
    val latitude: Double,
    @ColumnInfo(name = LONGITUDE)
    val longitude: Double,
    @ColumnInfo(name = ADDRESS)
    val address: String,
    @ColumnInfo(name = DRIVER_NAME)
    val driverName: String?,
    @ColumnInfo(name = PLATE)
    val plate: String,
    @ColumnInfo(name = SPEED)
    val speed: Long?,
    @ColumnInfo(name = LAST_ENGINE_ON_TIME)
    val lastEngineOnTime: String?,
) {

    companion object {
        const val TABLE_NAME = "last_data"

        const val OBJECT_ID = "object_id"
        const val TIMESTAMP = "timestamp"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val ADDRESS = "address"
        const val DRIVER_NAME = "driver_name"
        const val PLATE = "plate"
        const val SPEED = "speed"
        const val LAST_ENGINE_ON_TIME = "last_engine_on_time"
    }
}