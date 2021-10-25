package my.illrock.fcmapchallenge.presentation.trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import my.illrock.fcmapchallenge.data.entity.LastData
import my.illrock.fcmapchallenge.data.entity.RawData
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

    private var objectId: Long = 0L
    private var lastData: LastData? = null

    fun init(objectId: Long) {
        if (isInitialized()) return

        this.objectId = objectId
        lastDataRepository.get(false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                lastData = it.find { data -> data.objectId == objectId }
                _plate.value = lastData?.plate.orEmpty()
            }, {
                it.print()
            })

        onDatePick(Calendar.getInstance().time)
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
        rawDataRepository.get(objectId, begin, end)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _isLoading.value = true }
            .subscribe({
                _isLoading.value = false
                _tripData.value = it
                _isEmptyData.value = it.isEmpty()
            }, {
                _isLoading.value = false
                _isEmptyData.value = false
                it.print()
            })
    }
}