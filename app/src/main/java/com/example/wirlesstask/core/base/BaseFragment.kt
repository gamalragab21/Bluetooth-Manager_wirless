package com.example.wirlesstask.core.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<B : ViewBinding> : BaseViewBinding<B>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFragmentReady()
        subscribeToObservables()
    }

    abstract fun onFragmentReady()

    abstract fun subscribeToObservables()

    companion object {
        private const val TAG: String = "BaseFragment"
    }
}