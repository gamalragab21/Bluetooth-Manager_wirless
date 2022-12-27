package com.example.wirlesstask.ui.fragments.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wirlesstask.R
import com.example.wirlesstask.core.base.BaseFragment
import com.example.wirlesstask.core.extension.*
import com.example.wirlesstask.core.permissions.PermissionHelper
import com.example.wirlesstask.core.permissions.PermissionRule
import com.example.wirlesstask.databinding.FragmentHomeBinding
import com.example.wirlesstask.domain.client.BluetoothConnectionService
import com.example.wirlesstask.domain.dialoges.DialogUtils
import com.example.wirlesstask.domain.enum.DiscoverStatus.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), PermissionHelper.PermissionsCallback {

    private val appSettingLauncher = openAppIntentSettingsResultLauncher {
        requestBluetoothPermissions()
    }
    private val bluetoothSettingLauncher = openAppIntentSettingsResultLauncher {
        mViewModel.startInitBluetooth()
    }
    private val permissionLauncherActivity =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissionHelper.onPermissionsResult(permissions)
        }

    private val permissionHelper: PermissionHelper by lazy {
        PermissionHelper(requireActivity(), permissionLauncherActivity, this)
    }
    private val enableDiscoverability = openAppIntentSettingsResultLauncher {
        if (it.resultCode != 0)
            showSnackBar("Your Device in discoverability mode")
        else showSnackBar("Failed")
    }
    private val mViewModel: HomeVM by viewModels()

    @Inject
    lateinit var pairedDeviceAdapter: DeviceAdapter

    @Inject
    lateinit var availableDeviceAdapter: DeviceAdapter

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onFragmentReady() {
        setToolBarConfigs(
            R.string.mange_bluetooth, true,
            showActionButton = true,
            actionButtonIcon = R.drawable.visable
        )
        requestBluetoothPermissions()
        setupPairedDevicesRecyclerView()
        setupAvailableDevicesRecyclerView()
        onActionButtonClickListener {
            mViewModel.enableDiscoverability(enableDiscoverability)
        }
        onSwipeRefreshStatus(true)
        pairedDeviceAdapter.setOnItemSettingClickListener {
            BluetoothConnectionService().startServer()

        }
        availableDeviceAdapter.setOnItemSettingClickListener {
            AcceptThread().start()
//            BluetoothConnectionService().startServer()
//            navigateSafe(HomeFragmentDirections.actionHomeFragmentToSendFileDataFragment(it))
        }
    }

    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            mViewModel.bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("WirrLess",
                UUID.randomUUID())
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                     "Socket's accept() method failed: ${e.message}".toLog()
                    shouldLoop = false
                    null
                }
                socket?.also {
                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e("TAG", "Could not close the connect socket", e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun manageMyConnectedSocket(it: BluetoothSocket) {
        "manageMyConnectedSocket".toLog()
        it.connect()
    }

    override fun subscribeToObservables() {
        mViewModel.apply {
            observe(viewState) {
                handleViewState(it)
            }
        }
    }

    private fun handleViewState(homeState: HomeState) = when (homeState) {
        is HomeState.BluetoothStatus -> {
            if (homeState.status) {
                DialogUtils.dismissDialog()
                mViewModel.startInitBluetooth()
            } else DialogUtils.buildAlertAcceptBluetoothPermissions(requireContext()) {
                if (it) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    bluetoothSettingLauncher?.launch(enableBtIntent)
                } else requireActivity().finish()
            }
            stopSwipeRefreshLoading()
        }
        is HomeState.Failure -> showSnackBar(homeState.message ?: "Unknown error Occur!!")
        is HomeState.Loading -> showProgress(homeState.loading)
        is HomeState.AppendPairedDevices -> {
            stopSwipeRefreshLoading()
            if (homeState.devices == null || homeState.devices.isEmpty()) {
                binding.pairedDevicesRecyclerView.toGone()
                binding.tvNoPaired.toVisible()
            } else {
                binding.tvNoPaired.toGone()
                binding.pairedDevicesRecyclerView.toVisible()
                pairedDeviceAdapter.setItems(homeState.devices)
            }
        }
        is HomeState.DiscoverChanged -> {
            stopSwipeRefreshLoading()
            when (homeState.discoverStatus) {
                STARTED -> "stared".toLog("discoverStatus")
                FINISHED -> "FINISHED".toLog("discoverStatus")
                FAILED -> showSnackBar(getString(R.string.confirm_location_bluetooth_is_opend))
            }
        }
        is HomeState.AppendAvailableDevices -> {
            stopSwipeRefreshLoading()
            homeState.devices?.let { availableDeviceAdapter.addAll(listOf(it)) }
            if (availableDeviceAdapter.itemCount != 0) {
                binding.availableDevicesRecyclerView.toVisible()
                binding.tvNoAvailable.toGone()
            } else {
                binding.availableDevicesRecyclerView.toGone()
                binding.tvNoAvailable.toVisible()
            }
        }
        is HomeState.BluetoothDiscovery -> {
            stopSwipeRefreshLoading()
            updateActionButtonDrawable(
                if (homeState.discoveryStatus) {
                    R.drawable.visable
                } else R.drawable.invisible
            )
        }
    }

    override fun onRefreshView() {
           requestBluetoothPermissions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        permissionHelper.unRegister()
    }

    override fun isPermissionsRejected(
        permissionRule: PermissionRule,
        shouldOpenSettings: Boolean
    ) {
        DialogUtils.buildAlertAcceptLocationPermissions(requireContext()) {
            if (it) {
                if (shouldOpenSettings.not()) requestBluetoothPermissions()
                else appSettingLauncher?.launch(requireActivity().buildSettingNavigationIntent())
            } else {
                isPermissionsRejected(permissionRule, shouldOpenSettings)
            }
        }

    }

    private fun requestBluetoothPermissions() {
        permissionHelper.requestPermissions(PermissionRule.BLUETOOTH)
    }

    override fun onPermissionGranted(permissionRule: PermissionRule) {
        mViewModel.startInitBluetooth()
    }

    private fun setupPairedDevicesRecyclerView() = binding.pairedDevicesRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
        adapter = pairedDeviceAdapter
    }

    private fun setupAvailableDevicesRecyclerView() = binding.availableDevicesRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
        adapter = availableDeviceAdapter
    }

}