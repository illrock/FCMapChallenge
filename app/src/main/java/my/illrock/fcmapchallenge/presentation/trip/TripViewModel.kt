package my.illrock.fcmapchallenge.presentation.trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.illrock.fcmapchallenge.data.entity.LastData
import my.illrock.fcmapchallenge.data.entity.RawData
import my.illrock.fcmapchallenge.data.network.exception.InternalServerException
import my.illrock.fcmapchallenge.data.network.response.ResultWrapper
import my.illrock.fcmapchallenge.data.repository.LastDataRepository
import my.illrock.fcmapchallenge.data.repository.RawDataRepository
import my.illrock.fcmapchallenge.presentation.util.DateUtils
import my.illrock.fcmapchallenge.presentation.util.Event
import my.illrock.fcmapchallenge.util.print
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val rawDataRepository: RawDataRepository,
    private val lastDataRepository: LastDataRepository,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _tripData = MutableLiveData<List<RawData>>(listOf())
    val tripData: LiveData<List<RawData>> = _tripData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _plate = MutableLiveData("")
    val plate: LiveData<String> = _plate

    private val _isEmptyData = MutableLiveData(false)
    val isEmptyData: LiveData<Boolean> = _isEmptyData

    private val _chosenDate = MutableLiveData<Date?>(null)
    val chosenDate: LiveData<Date?> = _chosenDate

    private val _showDatePicker = MutableLiveData<Event<Date>>()
    val showDatePicker: LiveData<Event<Date>> = _showDatePicker

    private val _errorString = MutableLiveData<Event<String>>()
    val errorString: LiveData<Event<String>> = _errorString

    private var objectId: Long = 0L
    private var lastData: LastData? = null

    fun init(objectId: Long, defaultDate: Date? = null) {
        if (isInitialized()) return

        this.objectId = objectId

        viewModelScope.launch(dispatcher) {
            val result = LastData(lastDataRepository.getById(objectId))
            lastData = result
            withContext(Dispatchers.Main) {
                _plate.value = result.plate
            }
        }

        onDatePick(defaultDate ?: Calendar.getInstance().time)
    }

    fun onMapReady() {
        _tripData.value = _tripData.value
    }

    private fun isInitialized() = objectId != 0L

    fun onDatePickerClick() {
        val date = chosenDate.value ?: Calendar.getInstance().time
        _showDatePicker.value = Event(date)
    }

    fun onDatePick(beginDate: Date) {
        _chosenDate.value = beginDate

        val sdf = SimpleDateFormat(DateUtils.ISO_DATE_FORMAT, Locale.getDefault())
        val beginTimestamp = sdf.format(beginDate)

        val calendar = Calendar.getInstance()
        calendar.time = beginDate
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endDate = calendar.time
        val endTimestamp = sdf.format(endDate)

        loadTrip(beginTimestamp, endTimestamp)
    }

    private fun loadTrip(begin: String, end: String) {
        _isLoading.value = true
        viewModelScope.launch(dispatcher) {
            val result = rawDataRepository.get(objectId, begin, end)
            withContext(Dispatchers.Main) {
                handleRawDataResult(result)
            }
        }
    }

    private fun handleRawDataResult(result: ResultWrapper<List<RawData>>) {
        when (result) {
            is ResultWrapper.Success -> {
                _isLoading.value = false
                _tripData.value = result.data
                _isEmptyData.value = result.data.isEmpty()
            }
            is ResultWrapper.Error -> {
                _isLoading.value = false
                _isEmptyData.value = false

                val errorString =
                    if (result.exception is InternalServerException) result.exception.error
                    else null
                errorString?.let { _errorString.value = Event(it) }
                result.exception.print()
            }
        }
    }
}