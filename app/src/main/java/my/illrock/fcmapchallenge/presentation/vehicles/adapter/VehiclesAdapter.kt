package my.illrock.fcmapchallenge.presentation.vehicles.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import my.illrock.fcmapchallenge.R
import my.illrock.fcmapchallenge.data.entity.LastData
import my.illrock.fcmapchallenge.databinding.ItemVehicleBinding

class VehiclesAdapter(private val onClick: (LastData) -> Unit) :
    ListAdapter<LastData, VehiclesAdapter.VehicleViewHolder>(VehicleDiffCallback) {

    class VehicleViewHolder(private val binding: ItemVehicleBinding, val onClick: (LastData) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: LastData) {
            binding.tvPlate.text = data.plate
            with(binding) {
                tvPlate.text = data.plate
                data.driverName?.let {
                    tvDriver.isVisible = true
                    tvDriver.text = it
                } ?: run {
                    tvDriver.isVisible = false
                }
                tvAddress.text = data.address
                data.speed?.let {
                    tvSpeed.isVisible = true
                    tvSpeed.text = itemView.context.getString(R.string.speed_template, it)
                } ?: run {
                    tvSpeed.isVisible = false
                }
                data.lastEngineOnTime?.let {
                    tvDataAge.isVisible = true
                    tvDataAge.text = it
                } ?: run {
                    tvDataAge.isVisible = false
                }
            }
            itemView.setOnClickListener { onClick(data) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VehicleViewHolder(
        ItemVehicleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick
    )

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
}

object VehicleDiffCallback : DiffUtil.ItemCallback<LastData>() {
    override fun areItemsTheSame(oldItem: LastData, newItem: LastData) = oldItem == newItem
    override fun areContentsTheSame(oldItem: LastData, newItem: LastData) =
        oldItem.objectId == newItem.objectId
}