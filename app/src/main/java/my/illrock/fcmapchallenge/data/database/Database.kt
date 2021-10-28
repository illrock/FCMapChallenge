package my.illrock.fcmapchallenge.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import my.illrock.fcmapchallenge.data.database.Database.Companion.VERSION
import my.illrock.fcmapchallenge.data.database.lastdata.LastDataDao
import my.illrock.fcmapchallenge.data.database.lastdata.LastDataEntity

@Database(
    entities = [LastDataEntity::class],
    version = VERSION
)
abstract class Database : RoomDatabase() {
    abstract fun lastDataDao(): LastDataDao

    companion object {
        const val NAME = "fcmapchallenge.db"
        const val VERSION = 1
    }
}