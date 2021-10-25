package my.illrock.fcmapchallenge.presentation.apikey

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import my.illrock.fcmapchallenge.data.repository.ApiKeyRepository
import javax.inject.Inject

@HiltViewModel
class ApiKeyViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository
) : ViewModel() {
    private val _apiKey = MutableLiveData<String>()
    val apiKey: LiveData<String> = _apiKey

    fun getApiKey() {
        val key = apiKeyRepository.get()
        _apiKey.value = key
    }

    fun setApiKey(value: String) {
        apiKeyRepository.set(value)
    }
}