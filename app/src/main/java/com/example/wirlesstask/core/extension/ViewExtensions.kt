package com.example.wirlesstask.core.extension

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar

fun RecyclerView.disableItemChangedAnimation(disable: Boolean = true) {
    (itemAnimator as SimpleItemAnimator?)?.supportsChangeAnimations = !disable
}

fun Any?.toLog(tag: String = "FinPay") {
    Log.e(tag, ">> $this")
}

fun getColorFromString(color: String?): Int {
    if (color == null) return 0
    return try {
        Color.parseColor(color)
    } catch (e: Exception) {
        0
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun TextView.setTextSizeFromToPx(@DimenRes res: Int) =
    setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(res))

fun View.refreshView() {
    invalidate()
    requestLayout()
}

fun ImageView.setTint(@ColorInt color: Int?) {
    if (color == null) {
        ImageViewCompat.setImageTintList(this, null)
        return
    }
    ImageViewCompat.setImageTintMode(this, PorterDuff.Mode.SRC_IN)
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}


fun View.makeForeGroundClickable() {
    val outValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
    foreground = ContextCompat.getDrawable(context, outValue.resourceId)
}

fun View.show(show: Boolean = true) {
    if (show) toVisible() else toGone()
}

fun View.showInvisible(show: Boolean = true) {
    if (show) toVisible() else toInvisible()
}

fun View.toVisible() {
    visibility = View.VISIBLE
}

fun View.toGone() {
    visibility = View.GONE
}

fun View.toInvisible() {
    visibility = View.INVISIBLE
}


fun Fragment.showSnackBar(message: String, onRetryClicked: () -> Unit) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
        .setAction(androidx.navigation.dynamicfeatures.fragment.R.string.retry) { onRetryClicked() }
        .show()
}

fun View.showSnackBar(message: String, onRetryClicked: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(androidx.navigation.dynamicfeatures.fragment.R.string.retry) { onRetryClicked() }
        .show()
}

fun Fragment.showSnackBar(message: Int, onRetryClicked: () -> Unit) {
    Snackbar.make(requireView(), getString(message), Snackbar.LENGTH_INDEFINITE)
        .setAction(androidx.navigation.dynamicfeatures.fragment.R.string.retry) { onRetryClicked() }
        .show()
}

fun Fragment.showSnackBar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
}

infix fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.showSnackBar(message: Int) {
    Snackbar.make(requireView(), getString(message), Snackbar.LENGTH_SHORT).show()
}
