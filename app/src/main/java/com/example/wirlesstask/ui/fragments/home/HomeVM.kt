package com.example.wirlesstask.ui.fragments.home

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE
import android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.ACTION_FOUND
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.result.ActivityResultLauncher
import com.example.wirlesstask.core.base.AndroidBaseViewModel
import com.example.wirlesstask.core.extension.parcelable
import com.example.wirlesstask.core.extension.toLog
import com.example.wirlesstask.domain.enum.DiscoverStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeVM @Inject constructor(
    private val app: Application
) : AndroidBaseViewModel<HomeState>(app) {


    private val bluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                checkActionReceived(it)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkActionReceived(intent: Intent) {
        "checkActionReceived ${intent.action}".toLog()
        when (intent.action) {
            ACTION_FOUND -> {
                val device: BluetoothDevice =
                    intent.parcelable(BluetoothDevice.EXTRA_DEVICE)!!
                produce(HomeState.AppendAvailableDevices(device))
            }
            ACTION_STATE_CHANGED -> {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_ON -> produce(HomeState.BluetoothStatus(true))
                    BluetoothAdapter.STATE_OFF -> produce(HomeState.BluetoothStatus(false))
                }
            }
            BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED -> {

            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                produce(HomeState.Loading(false))
                produce(HomeState.BluetoothStatus(false))
            }
        }
    }

    var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetManager: BluetoothManager? = null

    fun startInitBluetooth() {
        produce(HomeState.Loading(true))
        "startInitBluetooth".toLog()
        bluetManager =
            app.getSystemService(BluetoothManager::class.java) as BluetoothManager
        bluetoothAdapter = bluetManager?.adapter
        if (bluetoothAdapter == null || bluetoothAdapter?.isEnabled == false) {
            produce(HomeState.BluetoothStatus(false))
            produce(HomeState.Loading(false))
        } else {
            queryPairedDevices()
            discoverDevices()
        }
    }

    @SuppressLint("MissingPermission")
    private fun discoverDevices() {
        val status =
            if (bluetoothAdapter?.startDiscovery() == true) DiscoverStatus.STARTED else DiscoverStatus.FAILED
        produce(HomeState.DiscoverChanged(status))
    }

    fun enableDiscoverability(enableDiscoverability: ActivityResultLauncher<Intent>?) {
        val discoverableIntent: Intent = Intent(ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        enableDiscoverability?.launch(discoverableIntent)

        queryPairedDevices()
        discoverDevices()
    }

    @SuppressLint("MissingPermission")
    private fun queryPairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        produce(HomeState.AppendPairedDevices(pairedDevices?.toList()))
        produce(HomeState.Loading(false))
    }


    private fun startBluetoothObservation() {
        "startBluetoothObservation".toLog()
        val filter = IntentFilter()
        filter.addAction(ACTION_STATE_CHANGED)
        filter.addAction(ACTION_FOUND)
        app.registerReceiver(bluetoothReceiver, filter)
    }


    init {
        startBluetoothObservation()
    }

    fun onDestroy() {
        app.unregisterReceiver(bluetoothReceiver)
    }


}