package com.example.wirlesstask.ui.fragments.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.example.wirlesstask.databinding.ItemDeviceLayoutBinding
import javax.inject.Inject

@SuppressLint("NotifyDataSetChanged")
class DeviceAdapter @Inject constructor(
    private val context: Context
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    private var devices: ArrayList<BluetoothDevice> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemDeviceLayoutBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    fun addAll(items: List<BluetoothDevice>) {
        for (item in items) {
            this.devices.add(item)
            notifyItemInserted(this.devices.lastIndex)
        }
    }

    fun setItems(items: List<BluetoothDevice>) {
        clear()
        for (item in items) {
            this.devices.add(item)
            notifyItemInserted(this.devices.lastIndex)
        }
    }

    fun clear() {
        devices.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = devices.size

    inner class ViewHolder(var binding: ItemDeviceLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.deviceSetting.setOnClickListener(this)
        }

        @SuppressLint("MissingPermission")
        fun bind(item: BluetoothDevice) = with(binding) {
            with(item) {
                tvDeviceName.text = name
                tvDeviceAddress.text = address
            }
        }

        override fun onClick(v: View) {
            val device = devices[adapterPosition]
            onItemSettingClickListener?.invoke(device)
        }
    }

    fun setOnItemSettingClickListener(listener: (BluetoothDevice) -> Unit) {
        onItemSettingClickListener = listener
    }

    private var onItemSettingClickListener: ((BluetoothDevice) -> Unit)? = null
}