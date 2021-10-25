package my.illrock.fcmapchallenge.presentation.apikey

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import my.illrock.fcmapchallenge.R
import my.illrock.fcmapchallenge.databinding.DialogApiKeyBinding
import my.illrock.fcmapchallenge.presentation.vehicles.VehiclesFragment


@AndroidEntryPoint
class ApiKeyDialogFragment : DialogFragment(R.layout.dialog_api_key) {
    private val vm: ApiKeyViewModel by viewModels()

    private var _binding: DialogApiKeyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogApiKeyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnOk.setOnClickListener { acceptApiKey() }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.etApiKey.apply {
            requestFocus()
            post {
                setSelection(length())
                showKeyboard()
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    acceptApiKey()
                    true
                } else false
            }
        }

        vm.apiKey.observe(viewLifecycleOwner) {
            binding.etApiKey.setText(it)
        }
        vm.getApiKey()
    }

    private fun acceptApiKey() {
        val key = binding.etApiKey.text.toString()
        vm.setApiKey(key)
        sendResult(key)
        dismiss()
    }

    private fun View.showKeyboard() {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun sendResult(key: String) {
        val result = bundleOf(VehiclesFragment.ARG_API_KEY_VALUE to key)
        parentFragmentManager.setFragmentResult(VehiclesFragment.REQUEST_KEY_API_KEY_EDIT, result)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}