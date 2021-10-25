package my.illrock.fcmapchallenge.data.repository

import android.text.format.DateUtils
import com.squareup.moshi.Moshi
import io.reactivex.rxjava3.core.Single
import my.illrock.fcmapchallenge.data.entity.LastData
import my.illrock.fcmapchallenge.data.network.ApiService
import my.illrock.fcmapchallenge.data.network.response.LastDataResponse
import my.illrock.fcmapchallenge.data.preference.PreferencesManager
import my.illrock.fcmapchallenge.data.provider.SystemClockProvider
import my.illrock.fcmapchallenge.util.fromJsonArray
import my.illrock.fcmapchallenge.util.toJsonArray
import java.lang.Math.abs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastDataRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager,
    private val moshi: Moshi,
    private val systemClockProvider: SystemClockProvider,
    private val apiKeyRepository: ApiKeyRepository
) {
    fun get(isForce: Boolean): Single<List<LastData>> {
        return if (isForce) getFromNetwork()
        else getFromDbOrNetwork()
    }

    fun clearCache() = preferencesManager.putLong(PREF_LAST_DATA_LAST_UPDATE_TIME, 0)

    //todo extract key
    private fun getFromNetwork(): Single<List<LastData>> {
        val apiKey = apiKeyRepository.get()
        return apiService.getLastData(apiKey)
            .doOnSuccess { updateCache(it) }
    }

    private fun getFromDbOrNetwork(): Single<List<LastData>> {
        return if (isOutdated()) getFromNetwork()
        else getFromDb()
    }

    /** Should use database(Room for example) with proper entities. Using prefs for simplicity */
    private fun getFromDb(): Single<List<LastData>> {
        val cachedResponse = moshi.fromJsonArray<LastData>(preferencesManager.getString(PREF_LAST_DATA_LAST_RESPONSE))
        return Single.just(cachedResponse)
    }

    private fun isOutdated(): Boolean {
        val lastUpdate = preferencesManager.getLong(PREF_LAST_DATA_LAST_UPDATE_TIME, 0L)
        return CACHE_LIFETIME < abs(systemClockProvider.elapsedRealtime() - lastUpdate)
    }

    private fun updateCache(response: List<LastData>) {
        val json = moshi.toJsonArray(response)
        preferencesManager.putString(PREF_LAST_DATA_LAST_RESPONSE, json)
        preferencesManager.putLong(PREF_LAST_DATA_LAST_UPDATE_TIME, systemClockProvider.elapsedRealtime())
    }

    companion object {
        private const val CACHE_LIFETIME = DateUtils.HOUR_IN_MILLIS
        private const val PREF_LAST_DATA_LAST_UPDATE_TIME = "last_data_last_update_time"
        private const val PREF_LAST_DATA_LAST_RESPONSE = "last_data_last_response"
    }
}