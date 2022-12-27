package com.example.wirlesstask.core.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


fun Fragment.replaceFragment(containerId: Int, fragment: Fragment) {
    activity?.supportFragmentManager?.commit {
        replace(containerId, fragment)
    }
}

fun <T : AppCompatActivity> Fragment.showActivity(
    activity: Class<T>,
    args: Bundle? = null,
    action: String? = null,
    clearAllStack: Boolean = false,
) {

    val intent = Intent(requireContext(), activity).apply {
        if (args != null) putExtras(args)
        if (action != null) setAction(action)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    if (clearAllStack) requireActivity().finish()

    startActivity(intent)
}

inline fun <reified T : AppCompatActivity> Fragment.castToActivity(
    callback: (T?) -> Unit,
): T? {
    return if (requireActivity() is T) {
        callback(requireActivity() as T)
        requireActivity() as T
    } else {
        callback(null)
        null
    }
}


val FragmentManager.currentNavigationFragments: MutableList<Fragment>?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments


/*This called when method onBackClick in activity is called */
fun Fragment.onBackClicked(callback: () -> Unit) {
    requireActivity().onBackClicked(callback)
}

fun ComponentActivity.onBackClicked(callback: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            callback.invoke()
        }
    })
}


// Binding View (Activity - Fragment) on the fly
// usually used in base Fragment/Activity
@Suppress("UNCHECKED_CAST")
fun <B : ViewBinding> LifecycleOwner.bindView(container: ViewGroup? = null): B {
    return if (this is Activity) {
        val inflateMethod = getTClass<B>().getMethod("inflate", LayoutInflater::class.java)
        val invokeLayout = inflateMethod.invoke(null, this.layoutInflater) as B
        this.setContentView(invokeLayout.root)
        invokeLayout
    } else {
        val fragment = this as Fragment
        val inflateMethod = getTClass<B>().getMethod(
            "inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java
        )
        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Cannot access view bindings. View lifecycle is ${lifecycle.currentState}!")
        }
        val invoke: B = inflateMethod.invoke(null, layoutInflater, container, false) as B
        invoke
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> Any.getTClass(): Class<T> {
    val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
    return type as Class<T>
}

fun Context.getDrawableFromRes(@DrawableRes drawable: Int): Drawable? =
    ContextCompat.getDrawable(this, drawable)

fun Context.getColorStateListFromRes(@ColorRes color: Int): ColorStateList {
    return ColorStateList.valueOf(getColorFromRes(color))
}

fun Context.getColorFromRes(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}
@SuppressLint("SuspiciousIndentation")
fun LifecycleOwner.openAppIntentSettingsResultLauncher(
    callBack: (result: ActivityResult) -> Unit
): ActivityResultLauncher<Intent>? {
    if (!this.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) return null
    val launcher: ActivityResultLauncher<Intent>
    val type = ActivityResultContracts.StartActivityForResult()
    val result = ActivityResultCallback<ActivityResult> {
        callBack.invoke(it)
    }
    launcher = when (this) {
        is Fragment -> this.registerForActivityResult(type, result)
        is AppCompatActivity -> {
            this.registerForActivityResult(type, result)
        }
        else -> throw IllegalAccessException("must be called from a Fragment or AppCompatActivity")
    }
    return launcher
}
