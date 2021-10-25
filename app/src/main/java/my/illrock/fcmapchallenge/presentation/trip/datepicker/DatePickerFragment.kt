package my.illrock.fcmapchallenge.presentation.trip.datepicker

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import my.illrock.fcmapchallenge.presentation.trip.TripFragment
import my.illrock.fcmapchallenge.presentation.util.DateUtils
import my.illrock.fcmapchallenge.presentation.vehicles.VehiclesFragment
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val date = arguments?.getSerializable(ARG_DATE) as Date?
        date?.let { calendar.time = it }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        sendResult(year, month, dayOfMonth)
        dismiss()
    }

    private fun sendResult(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val result = bundleOf(
            REQUEST_ARG_TRIP_DATE_VALUE to calendar.time,
        )
        parentFragmentManager.setFragmentResult(REQUEST_KEY_TRIP_DATE_PICKED, result)
    }

    companion object {
        const val REQUEST_KEY_TRIP_DATE_PICKED = "request_key_trip_date_picked"
        const val REQUEST_ARG_TRIP_DATE_VALUE = "request_arg_trip_date_year"

        private const val ARG_DATE = "arg_date"
        operator fun invoke(date: Date? = null) = DatePickerFragment().apply {
            arguments = bundleOf(ARG_DATE to date)
        }
    }
}