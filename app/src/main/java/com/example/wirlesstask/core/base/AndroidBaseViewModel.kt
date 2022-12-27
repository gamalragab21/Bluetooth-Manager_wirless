package com.example.wirlesstask.core.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

interface State

interface CurrentAction

abstract class AndroidBaseViewModel<S : State>(app: Application) : AndroidViewModel(app) {

    private val _viewState = MutableSharedFlow<S>()
    val viewState: SharedFlow<S> = _viewState

    //use this method to produce an action to the view
    fun produce(s: S) {
        viewModelScope.launch(Dispatchers.Main) {
            _viewState.emit(s)
        }
    }

    // to switch thread in test
    open fun getIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}