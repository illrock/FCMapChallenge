package my.illrock.fcmapchallenge.data.repository

import io.reactivex.rxjava3.core.Single
import my.illrock.fcmapchallenge.data.entity.RawData
import my.illrock.fcmapchallenge.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RawDataRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiKeyRepository: ApiKeyRepository
) {
    fun get(objectId: Long, begin: String, end: String) = getFromNetwork(objectId, begin, end)

    private fun getFromNetwork(objectId: Long, begin: String, end: String): Single<List<RawData>> {
        val apiKey = apiKeyRepository.get()
        return apiService.getRawData(objectId, begin, end, apiKey)
    }
}