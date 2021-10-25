package my.illrock.fcmapchallenge.presentation.vehicles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import my.illrock.fcmapchallenge.R
import my.illrock.fcmapchallenge.data.entity.LastData
import my.illrock.fcmapchallenge.data.network.exception.InternalServerException
import my.illrock.fcmapchallenge.data.repository.ApiKeyRepository
import my.illrock.fcmapchallenge.data.repository.LastDataRepository
import my.illrock.fcmapchallenge.presentation.util.Event
import my.illrock.fcmapchallenge.util.print
import javax.inject.Inject

@HiltViewModel
class VehiclesViewModel @Inject constructor(
    private val lastDataRepository: LastDataRepository,
    private val apiKeyRepository: ApiKeyRepository
) : ViewModel() {

    private var lastDataDisposable = Disposable.disposed()

    private val _lastData = MutableLiveData<List<LastData>>(listOf())
    val lastData: LiveData<List<LastData>> = _lastData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorRes = MutableLiveData<Event<Int?>>(Event(null))
    val errorRes: LiveData<Event<Int?>> = _errorRes

    private val _errorString = MutableLiveData<Event<String?>>(Event(null))
    val errorString: LiveData<Event<String?>> = _errorString

    private val _showApiKeyDialog = MutableLiveData<Event<Boolean>>()
    val showApiKeyDialog: LiveData<Event<Boolean>> = _showApiKeyDialog

    private val _showEmptyApiKeyMessage = MutableLiveData<Boolean>()
    val showEmptyApiKeyMessage: LiveData<Boolean> = _showEmptyApiKeyMessage

    private val _showUnknownApiKeyMessage = MutableLiveData<Boolean>()
    val showUnknownApiKeyMessage: LiveData<Boolean> = _showUnknownApiKeyMessage

    private var apiKey = ""

    fun init() {
        apiKey = apiKeyRepository.get()
        if (apiKey.isEmpty()) {
            _showEmptyApiKeyMessage.value = true
            _showApiKeyDialog.value = Event(true)
        } else {
            _showEmptyApiKeyMessage.value = false
            updateLastData(false)
        }
    }

    fun updateLastData(isForce: Boolean, newApiKey: String = apiKey) {
        if (newApiKey.isEmpty()) {
            hideErrors()
            _lastData.value = listOf()
            _showEmptyApiKeyMessage.value = true
        } else {
            _showEmptyApiKeyMessage.value = false
            loadData(isForce)
        }
    }

    private fun loadData(isForce: Boolean) {
        lastDataDisposable.dispose()
        lastDataDisposable = lastDataRepository.get(isForce)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _isLoading.value = true
                hideErrors()
            }.subscribe({
                _isLoading.value = false
                _lastData.value = it
                apiKey = apiKeyRepository.get()
            }, {
                _isLoading.value = false
                it.print()
                if (it is InternalServerException) {
                    _errorString.value = Event(it.error)
                    _errorRes.value = Event(null)
//                    if (_showEmptyApiKeyMessage.value == false) {
                        _showUnknownApiKeyMessage.value = it.isUnknownApiException()
//                    }
                } else {
                    _errorString.value = Event(null)
                    _errorRes.value = Event(R.string.error_unknown)
                }
                apiKey = apiKeyRepository.get()
                lastDataRepository.clearCache()
            })
    }

    private fun hideErrors() {
        _errorString.value = Event(null)
        _errorRes.value = Event(null)
        _showUnknownApiKeyMessage.value = false
    }

    fun onApiKeyClick() {
        _showApiKeyDialog.value = Event(true)
    }

    fun onNewApiKey(newKey: String) {
        _showEmptyApiKeyMessage.value = newKey.isEmpty()

        if (newKey != apiKey) {
            updateLastData(true, newKey)
            _lastData.value = listOf()
        }
    }

    override fun onCleared() {
        lastDataDisposable.dispose()
    }
}