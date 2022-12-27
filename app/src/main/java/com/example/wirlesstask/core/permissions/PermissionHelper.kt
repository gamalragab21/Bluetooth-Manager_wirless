package com.example.wirlesstask.core.permissions

import android.os.Build
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

class PermissionHelper(
    private val activity: FragmentActivity,
    private val permissionLauncher: ActivityResultLauncher<Array<String>>,
    private val callback: PermissionsCallback
) {

    private lateinit var permissionRule: PermissionRule

    fun requestPermissions(permissionRule: PermissionRule) {
        this.permissionRule = permissionRule
        if (checkRequestedPermissions()) onPermissionGranted()
        else requestDesiredPermissions()
    }

    private fun checkRequestedPermissions(): Boolean {
        return if (permissionRule == PermissionRule.READ_WRITE_STORAGE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Environment.isExternalStorageManager()
        else activity.isPermissionsGranted(permissionRule)
    }

    private fun requestDesiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissionLauncher.launch(permissionRule.permissions)
        } else {
            // below android 11
            ActivityCompat.requestPermissions(
                activity, permissionRule.permissions, PERMISSION_CODE_RESULT
            )
        }
    }

    private fun validateOnPermissionsResults(permissions: MutableList<Boolean>) {
        if (permissions.all { it }) {
            // perform action when allow permission success
            onPermissionGranted()
        } else {
            callback.isPermissionsRejected(
                permissionRule,
                activity.shouldShowRequestPermissionRationale(permissionRule.permissions[0]).not()
            )
        }
    }

    private fun onPermissionGranted() {
        callback.onPermissionGranted(permissionRule)
    }

    fun unRegister() = permissionLauncher.unregister()

    fun onPermissionsResult(permissions: Map<String, @JvmSuppressWildcards Boolean>) {
        val list = mutableListOf<Boolean>()
        permissions.entries.forEach { list.add(it.value) }
        validateOnPermissionsResults(list)
    }

    interface PermissionsCallback {
        fun isPermissionsRejected(
            permissionRule: PermissionRule, shouldOpenSettings: Boolean = false
        )

        fun onPermissionGranted(permissionRule: PermissionRule)
    }

    companion object {
        private const val PERMISSION_CODE_RESULT = 164
    }
}