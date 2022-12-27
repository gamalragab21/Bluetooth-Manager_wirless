package com.example.wirlesstask.core.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar



fun Context.buildSettingNavigationIntent(): Intent {
    return Intent(
        Settings.ACTION_APP_SEARCH_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}
