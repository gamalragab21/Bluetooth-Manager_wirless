package com.example.wirlesstask.domain.client

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.wirlesstask.core.extension.toLog
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer

class BluetoothClient(device: BluetoothDevice): Thread() {

    @SuppressLint("MissingPermission")
    private val socket = device.createRfcommSocketToServiceRecord(BluetoothConnectionService.uuid)

    @SuppressLint("MissingPermission")
    override fun run() {
        try {
            this.socket.connect()
        } catch (e: IOException) {
            e.printStackTrace().toLog("IOException")
            return
        }

        val outputStream = this.socket.outputStream
        val inputStream = this.socket.inputStream
        val file = File(BluetoothConnectionService.fileURI)
        val fileBytes: ByteArray
        try {
            fileBytes = ByteArray(file.length().toInt())
            val bufferedInputStream = BufferedInputStream(FileInputStream(file))
            bufferedInputStream.read(fileBytes, 0, fileBytes.size)
            bufferedInputStream.close()
        } catch (e: IOException) {
            return
        }

        val fileNameSize = ByteBuffer.allocate(4)
        fileNameSize.putInt(file.name.toByteArray().size)

        val fileSize = ByteBuffer.allocate(4)
        fileSize.putInt(fileBytes.size)

        outputStream.write(fileNameSize.array())
        outputStream.write(file.name.toByteArray())
        outputStream.write(fileSize.array())
        outputStream.write(fileBytes)

        BluetoothConnectionService.isSuccess = true

        sleep(5000)
        outputStream.close()
        inputStream.close()
        this.socket.close()
    }
}
