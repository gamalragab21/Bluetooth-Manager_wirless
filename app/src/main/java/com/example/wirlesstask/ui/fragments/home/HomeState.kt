package com.example.wirlesstask.ui.fragments.home

import android.bluetooth.BluetoothDevice
import com.example.wirlesstask.core.base.State
import com.example.wirlesstask.domain.enum.DiscoverStatus

sealed class HomeState : State {
    data class Loading(val loading: Boolean) : HomeState()
    data class Failure(val message: String?) :
        HomeState()

    data class BluetoothStatus(val status: Boolean) : HomeState()
    data class AppendPairedDevices(val devices: List<BluetoothDevice>?=null) : HomeState()
    data class AppendAvailableDevices(val devices: BluetoothDevice ?=null) : HomeState()
    data class DiscoverChanged(val discoverStatus: DiscoverStatus =DiscoverStatus.STARTED) : HomeState()
    data class BluetoothDiscovery(val discoveryStatus: Boolean) : HomeState()

}