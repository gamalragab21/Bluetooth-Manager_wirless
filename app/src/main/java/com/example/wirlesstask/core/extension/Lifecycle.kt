package com.example.wirlesstask.core.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

fun <T : Any?, L : SharedFlow<T>> LifecycleOwner.observe(sharedFlow: L, body: (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            sharedFlow.collect {
                body.invoke(it)
            }
        }
    }
}

fun <T : Any?, L : LiveData<T?>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
    liveData.observe(
        if (this is Fragment) viewLifecycleOwner else this,
        Observer {
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                body(it)
            }
        }
    )
}



