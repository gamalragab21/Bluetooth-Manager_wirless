package com.example.wirlesstask.domain.client

import android.bluetooth.BluetoothDevice
import com.example.wirlesstask.core.extension.toLog
import com.example.wirlesstask.domain.server.BluetoothServerController
import java.util.*

class BluetoothConnectionService {
    companion object {
        val uuid: UUID = UUID.fromString("8989063a-c9af-463a-b3f1-f21d9b2b827b")
        var fileURI: String = ""
        var isSuccess: Boolean = false
    }

    fun startServer() {
        BluetoothServerController().start()
    }

    fun startClient(device: BluetoothDevice, uri: String): Boolean {
        fileURI = uri

        val bluetoothClient = BluetoothClient(device)
        bluetoothClient.start()
        try {
            bluetoothClient.join()
        } catch (e: InterruptedException) {
            e.printStackTrace().toLog("InterruptedException")
            println(e)
        }
        return isSuccess
    }
}