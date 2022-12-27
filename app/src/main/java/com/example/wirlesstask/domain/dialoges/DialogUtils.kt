package com.example.wirlesstask.domain.dialoges

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.wirlesstask.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtils {
    var dialog: AlertDialog? = null
    fun buildAlertAcceptLocationPermissions(
        context: Context, onButtonClickedListener: (okClicked: Boolean) -> Unit
    ) {
        dismissDialog()
        MaterialAlertDialogBuilder(context).setTitle(context.getString(R.string.permission_denied))
            .setMessage(context.getString(R.string.you_need_to_allow_using_device_location_bluetooth_to_continue))
            .setCancelable(false)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
                onButtonClickedListener(true)
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
                onButtonClickedListener(false)
            }.apply {
                dialog = show()
            }
    }

    fun buildAlertAcceptBluetoothPermissions(
        context: Context, onButtonClickedListener: (okClicked: Boolean) -> Unit
    ) {
        dismissDialog()
        MaterialAlertDialogBuilder(context).setTitle(context.getString(R.string.permission_denied))
            .setMessage(context.getString(R.string.you_need_to_open_bluetooth_to_continue))
            .setCancelable(false)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
                onButtonClickedListener(true)
            }.setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                onButtonClickedListener(false)
            }.apply {
                dialog = show()
            }

    }

    fun dismissDialog() = dialog?.dismiss()

}