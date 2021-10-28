package my.illrock.fcmapchallenge.presentation.trip

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ktx.addMarker
import dagger.hilt.android.AndroidEntryPoint
import my.illrock.fcmapchallenge.R
import my.illrock.fcmapchallenge.data.entity.RawData
import my.illrock.fcmapchallenge.databinding.FragmentTripBinding
import my.illrock.fcmapchallenge.presentation.trip.datepicker.DatePickerFragment
import my.illrock.fcmapchallenge.presentation.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TripFragment : Fragment(R.layout.fragment_trip) {
    private val vm: TripViewModel by viewModels()

    private var _binding: FragmentTripBinding? = null
    private val binding get() = _binding!!

    private var gMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ivBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.llDatePicker.setOnClickListener { vm.onDatePickerClick() }
        binding.tvEmptyDataMessage.setOnClickListener { vm.onDatePickerClick() }
        initMap()
        setupViewModel(view)
        setDatePickerResultListener()
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fMap) as SupportMapFragment
        mapFragment.getMapAsync {
            gMap = it
            vm.onMapReady()
        }
    }

    private fun setupViewModel(view: View) {
        val objectId = arguments?.getLong(ARG_OBJECT_ID) as Long
        vm.init(objectId)

        vm.plate.observe(viewLifecycleOwner) {
            binding.tvTitle.text = getString(R.string.trip_title_template, it)
        }

        vm.tripData.observe(viewLifecycleOwner) { dataList ->
            binding.flMapContainer.isVisible = dataList.isNotEmpty()
            if (dataList.isEmpty()) return@observe
            gMap?.showData(dataList)
        }

        vm.isLoading.observe(viewLifecycleOwner) {
            binding.pbLoading.isVisible = it
            if (it) binding.tvEmptyDataMessage.isVisible = false
        }

        vm.isEmptyData.observe(viewLifecycleOwner) {
            binding.tvEmptyDataMessage.isVisible = it
            if (it) binding.tvDistance.text = ""
        }

        vm.chosenDate.observe(viewLifecycleOwner) {
            it?.let { date ->
                val sdf = SimpleDateFormat(DateUtils.CALENDAR_DATE_FORMAT, Locale.getDefault())
                binding.tvDate.text = sdf.format(date)
            }
        }

        vm.showDatePicker.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { date ->
                DatePickerFragment(date).show(parentFragmentManager, null)
            }
        }

        vm.errorString.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { error ->
                Snackbar.make(view, error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setDatePickerResultListener() {
        parentFragmentManager.setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_TRIP_DATE_PICKED,
            this@TripFragment
        ) { key, args ->
            val date = args.getSerializable(DatePickerFragment.REQUEST_ARG_TRIP_DATE_VALUE) as Date?
            date?.let { vm.onDatePick(it) }
        }
    }

    private fun GoogleMap.showData(dataList: List<RawData>) {
        clear()
        val filteredData = dataList
            // I was surprised that some data entries comes without coordinates
            .filter { it.latitude != null && it.longitude != null }
            .map { LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0) }

        addPolyline(
            PolylineOptions()
                .addAll(filteredData)
                .color(ContextCompat.getColor(requireContext(), R.color.map_route))
        )

        if (filteredData.isNotEmpty()) {
            addPin(filteredData.first(), getString(R.string.trip_pin_start))
            if (filteredData.size > 1) addPin(filteredData.last(), getString(R.string.trip_pin_end))
            val distance = SphericalUtil.computeLength(filteredData).toLong()
            binding.tvDistance.text = getString(R.string.trip_distance_template, distance)
        } else {
            binding.tvDistance.text = ""
        }

        setOnMapLoadedCallback {
            val bounds = LatLngBounds.builder()
            filteredData.forEach { bounds.include(it) }
            val padding = resources.getDimensionPixelSize(R.dimen.map_trip_padding)
            moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), padding))
        }
    }

    private fun GoogleMap.addPin(coordinates: LatLng, title: String?) {
        addMarker {
            position(coordinates)
            title?.let { title(it) }
            icon(
                vectorToBitmap(
                    requireContext(),
                    R.drawable.ic_map_pin,
                    R.color.map_pin
                )
            )
        }
    }

    // Why gmaps can't parse vectors by itself??
    private fun vectorToBitmap(
        context: Context,
        @DrawableRes id: Int,
        @ColorRes colorRes: Int
    ): BitmapDescriptor {
        val colorInt = ContextCompat.getColor(context, colorRes)
        val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, null)
            ?: return BitmapDescriptorFactory.defaultMarker()
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, colorInt)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_OBJECT_ID = "arg_object_id"
        operator fun invoke(objectId: Long) = TripFragment().apply {
            arguments = bundleOf(ARG_OBJECT_ID to objectId)
        }
    }
}