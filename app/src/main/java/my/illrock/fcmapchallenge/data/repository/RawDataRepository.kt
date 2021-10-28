package my.illrock.fcmapchallenge.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.illrock.fcmapchallenge.data.entity.RawData
import my.illrock.fcmapchallenge.data.network.ApiService
import my.illrock.fcmapchallenge.data.network.response.ResultWrapper
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RawDataRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiKeyRepository: ApiKeyRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun get(objectId: Long, begin: String, end: String) = withContext(dispatcher) {
        getFromNetwork(objectId, begin, end)
    }

    private suspend fun getFromNetwork(objectId: Long, begin: String, end: String): ResultWrapper<List<RawData>> {
        val apiKey = apiKeyRepository.get()
        return try {
            val result = apiService.getRawData(objectId, begin, end, apiKey)
            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(e)
        }
    }
}