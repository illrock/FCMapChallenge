package my.illrock.fcmapchallenge.presentation.vehicles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import my.illrock.fcmapchallenge.R
import my.illrock.fcmapchallenge.data.entity.LastData
import my.illrock.fcmapchallenge.databinding.FragmentVehiclesBinding
import my.illrock.fcmapchallenge.presentation.apikey.ApiKeyDialogFragment
import my.illrock.fcmapchallenge.presentation.trip.TripFragment
import my.illrock.fcmapchallenge.presentation.vehicles.adapter.VehiclesAdapter

@AndroidEntryPoint
class VehiclesFragment : Fragment(R.layout.fragment_vehicles) {
    private val vm: VehiclesViewModel by viewModels()

    private var _binding: FragmentVehiclesBinding? = null
    private val binding get() = _binding!!

    private lateinit var vehiclesAdapter: VehiclesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVehiclesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvVehicles.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            vehiclesAdapter = VehiclesAdapter(::onItemClick)
            adapter = vehiclesAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }

        binding.srlPull.setOnRefreshListener { vm.updateLastData(true) }
        binding.ivRefresh.setOnClickListener { vm.updateLastData(true) }
        binding.ivApiKey.setOnClickListener { vm.onApiKeyClick() }
        binding.tvEmptyApiKeyMessage.setOnClickListener { vm.onApiKeyClick() }
        binding.tvUnknownApiKeyMessage.setOnClickListener { vm.onApiKeyClick() }
        setApiKeyResultListener()
        setupViewModel(view)
    }

    private fun setupViewModel(view: View) {
        vm.lastData.observe(viewLifecycleOwner) {
            vehiclesAdapter.submitList(it)
        }

        vm.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading()
            else hideLoading()
        }

        vm.errorRes.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { errorRes ->
                Snackbar.make(view, errorRes, Snackbar.LENGTH_LONG).show()
            }
        }

        vm.errorString.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { error ->
                Snackbar.make(view, error, Snackbar.LENGTH_LONG).show()
            }
        }

        vm.showApiKeyDialog.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                ApiKeyDialogFragment().show(parentFragmentManager, null)
            }
        }

        vm.showEmptyApiKeyMessage.observe(viewLifecycleOwner) {
            binding.tvEmptyApiKeyMessage.isVisible = it
            binding.srlPull.isEnabled = !it
        }

        vm.showUnknownApiKeyMessage.observe(viewLifecycleOwner) {
            binding.tvUnknownApiKeyMessage.isVisible = it
            binding.srlPull.isEnabled = !it
        }

        vm.init()
    }

    private fun setApiKeyResultListener() {
        parentFragmentManager.setFragmentResultListener(REQUEST_KEY_API_KEY_EDIT, this@VehiclesFragment) { key, args ->
            val apiKey = args.getString(ARG_API_KEY_VALUE)
            apiKey?.let { vm.onNewApiKey(it) }
        }
    }

    private fun showLoading() {
        if (vehiclesAdapter.itemCount == 0) {
            binding.pbLoading.isVisible = true
        } else {
            binding.srlPull.isRefreshing = true
        }
    }

    private fun hideLoading() {
        binding.pbLoading.isVisible = false
        binding.srlPull.isRefreshing = false
    }

    private fun onItemClick(data: LastData) {
        parentFragmentManager.commit {
            replace(R.id.fcvMain, TripFragment(data.objectId))
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val REQUEST_KEY_API_KEY_EDIT = "request_key_api_key_edit"
        const val ARG_API_KEY_VALUE = "arg_api_key_value"
    }
}