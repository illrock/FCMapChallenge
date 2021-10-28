package my.illrock.fcmapchallenge.data.repository

import android.text.format.DateUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import my.illrock.fcmapchallenge.data.database.lastdata.LastDataDao
import my.illrock.fcmapchallenge.data.entity.LastData
import my.illrock.fcmapchallenge.data.network.ApiService
import my.illrock.fcmapchallenge.data.network.response.ResultWrapper
import my.illrock.fcmapchallenge.data.preference.PreferencesManager
import my.illrock.fcmapchallenge.data.provider.SystemClockProvider
import java.lang.Math.abs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastDataRepository @Inject constructor(
    private val apiService: ApiService,
    private val lastDataDao: LastDataDao,
    private val preferencesManager: PreferencesManager,
    private val systemClockProvider: SystemClockProvider,
    private val apiKeyRepository: ApiKeyRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getAll(isForce: Boolean): ResultWrapper<List<LastData>> = withContext(dispatcher) {
        if (isForce) getFromNetwork()
        else getFromDbOrNetwork()
    }

    suspend fun getById(objectId: Long) = lastDataDao.getById(objectId)

    fun clearCache() = preferencesManager.putLong(PREF_LAST_DATA_LAST_UPDATE_TIME, 0)

    private suspend fun getFromNetwork(): ResultWrapper<List<LastData>> {
        lastDataDao.deleteAll()
        val apiKey = apiKeyRepository.get()
        return try {
            val data = apiService.getLastData(apiKey)
                .sort()
            updateDb(data)
            ResultWrapper.Success(data)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }

    private suspend fun getFromDbOrNetwork(): ResultWrapper<List<LastData>> {
        return if (isOutdated()) getFromNetwork()
        else getFromDb()
    }

    private suspend fun getFromDb(): ResultWrapper<List<LastData>> {
        val dbData = lastDataDao.getAll().map { LastData(it) }
            .sort()
        return ResultWrapper.Success(dbData)
    }

    private fun isOutdated(): Boolean {
        val lastUpdate = preferencesManager.getLong(PREF_LAST_DATA_LAST_UPDATE_TIME, 0L)
        return CACHE_LIFETIME < abs(systemClockProvider.elapsedRealtime() - lastUpdate)
    }

    private suspend fun updateDb(response: List<LastData>) {
        val entities = response.map { it.toDbEntity() }
        lastDataDao.insert(entities)
        preferencesManager.putLong(PREF_LAST_DATA_LAST_UPDATE_TIME, systemClockProvider.elapsedRealtime())
    }

    private fun List<LastData>.sort() = sortedByDescending { it.lastEngineOnTime }

    companion object {
        private const val CACHE_LIFETIME = DateUtils.HOUR_IN_MILLIS
        private const val PREF_LAST_DATA_LAST_UPDATE_TIME = "last_data_last_update_time"
    }
}