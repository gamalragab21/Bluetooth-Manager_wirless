package com.example.wirlesstask.core.permissions

import android.Manifest.*
import android.Manifest.permission.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


fun Context.isPermissionsGranted(permissionRule: PermissionRule): Boolean {
    val list = arrayListOf<Boolean>()
    permissionRule.permissions.onEach { list.add(isPermissionGranted(it)) }
    return list.all { it }
}

private fun Context.isPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

enum class PermissionRule(val permissions: Array<String>) {
    READ_WRITE_STORAGE(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)),

    @RequiresApi(Build.VERSION_CODES.R)
    MANAGE_STORAGE(arrayOf(MANAGE_EXTERNAL_STORAGE)),

    LOCATION(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)),

    BLUETOOTH(arrayOf(LOCATION.permissions.iterator().next(),BLUETOOTH_CONNECT, BLUETOOTH_SCAN))
}