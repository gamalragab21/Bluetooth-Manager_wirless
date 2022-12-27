package com.example.wirlesstask.core.base

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import com.example.wirlesstask.core.extension.bindView

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {

    private var _binding: B? = null
    protected val binding: B get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        _binding = bindView()
        setContentView(binding.root)
        viewInit()
        onActivityReady(savedInstanceState)
    }

    abstract fun onActivityReady(savedInstanceState: Bundle?)

    abstract fun viewInit()

    open fun onLoading(loading: Boolean = false) {
        when (loading) {
            true -> closeTouche()
            false -> openTouche()
        }
    }

    private fun closeTouche() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun openTouche() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}