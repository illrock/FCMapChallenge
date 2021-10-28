package my.illrock.fcmapchallenge.data.database.lastdata

import androidx.room.*

@Dao
interface LastDataDao {
    @Query("SELECT * FROM ${LastDataEntity.TABLE_NAME}")
    suspend fun getAll(): List<LastDataEntity>

    @Query("SELECT * FROM ${LastDataEntity.TABLE_NAME} WHERE object_id = :objectId")
    suspend fun getById(objectId: Long): LastDataEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lastDataList: List<LastDataEntity>)

    @Query("DELETE FROM ${LastDataEntity.TABLE_NAME}")
    suspend fun deleteAll()
}