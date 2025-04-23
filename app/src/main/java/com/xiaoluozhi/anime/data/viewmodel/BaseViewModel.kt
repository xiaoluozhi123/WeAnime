package com.xiaoluozhi.anime.data.viewmodel

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Keep
interface BaseState

@Keep
interface BaseIntent

@Keep
abstract class BaseViewModel<State : BaseState, Intent : BaseIntent> : ViewModel() {

    private val _state = MutableStateFlow(this.initState())
    val state: StateFlow<State> = _state.asStateFlow()

    private val intentChannel = Channel<Intent>(Channel.UNLIMITED)
    private val intent: Flow<Intent> = intentChannel.receiveAsFlow()

    protected abstract fun initState(): State

    protected fun setState(copy: State.() -> State) {
        _state.update { copy(_state.value) }
    }

    fun sendIntent(intent: Intent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    init {
        viewModelScope.launch {
            intent.collect { intent ->
                handleIntent(intent)
            }
        }
    }

    protected abstract fun handleIntent(intent: Intent)
}
