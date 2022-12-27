package com.example.wirlesstask.core.extension

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.*
import androidx.navigation.fragment.findNavController
import com.example.wirlesstask.R

fun LifecycleOwner.navigateSafe(directions: NavDirections, navOptions: NavOptions? = null) {
    val navController: NavController?
    val mView: View?
    if (this is Fragment) {
        navController = findNavController()
        mView = view
    } else {
        val activity = this as Activity
        navController = activity.findNavController(R.id.fragment_container_view)
        mView = currentFocus
    }
    if (canNavigate(navController, mView)) navController.navigate(directions, navOptions)
}

fun LifecycleOwner.navigateSafe(
    @IdRes navFragmentRes: Int,
    bundle: Bundle? = null,
    navOptions: NavOptions? = null,
    navExtras: Navigator.Extras? = null,
) {
    val navController: NavController?
    val mView: View?
    if (this is Fragment) {
        navController = findNavController()
        mView = view
    } else {
        val activity = this as Activity
        navController = activity.findNavController(R.id.fragment_container_view)
        mView = currentFocus
    }
    if (canNavigate(navController, mView)) {
        val currentDest = navController.currentDestination?.id
        if (currentDest != null && currentDest != navFragmentRes)
            navController.navigate(navFragmentRes, bundle, navOptions, navExtras)
    }
}

fun LifecycleOwner.popUpCurrentFragment(): NavOptions? {
    return if (this is Fragment) NavOptions.Builder()
        .setPopUpTo(findNavController().currentDestination?.id!!, true).build()
    else null
}

private fun canNavigate(controller: NavController, view: View?): Boolean {
    val destinationIdInNavController = controller.currentDestination?.id
    // add tag_navigation_destination_id to your res\values\ids.xml so that it's unique:
    val destinationIdOfThisFragment =
        view?.getTag(R.id.tag_navigation_destination_id) ?: destinationIdInNavController

    // check that the navigation graph is still in 'this' fragment, if not then the app already navigated:
    return if (destinationIdInNavController == destinationIdOfThisFragment) {
        view?.setTag(R.id.tag_navigation_destination_id, destinationIdOfThisFragment)
        true
    } else {
        "May not navigate: current destination is not the current fragment.".toLog()
        false
    }
}

fun Activity.navToActivity(
    destinationActivity: Class<out AppCompatActivity>,
    bundle: Bundle? = null,
    clearStacks: Boolean = false
) {
    val intent = Intent(this, destinationActivity)
    bundle?.let { intent.putExtras(it) }
    startActivity(intent)
    if (clearStacks) finishAffinity()
}

