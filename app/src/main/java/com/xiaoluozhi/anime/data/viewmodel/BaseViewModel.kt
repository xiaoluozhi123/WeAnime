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

/**
 * 标记接口，用于表示视图的状态
 */
@Keep
interface BaseState

/**
 * 标记接口，用于表示视图的意图
 */
@Keep
interface BaseIntent

/**
 * 抽象基类 ViewModel，所有 ViewModel 都要继承该类
 * @param State 视图状态类型，需要实现 BaseState 接口
 * @param Intent 视图意图类型，需要实现 BaseIntent 接口
 */
@Keep
abstract class BaseViewModel<State : BaseState, Intent : BaseIntent> : ViewModel() {

    // 内部可变的视图状态流
    private val _state = MutableStateFlow(this.initState())
    // 公开的不可变视图状态流
    val state: StateFlow<State> = _state.asStateFlow()

    // 用于发送意图的通道，无限容量
    private val intentChannel = Channel<Intent>(Channel.UNLIMITED)
    // 用于接收意图的流
    private val intent: Flow<Intent> = intentChannel.receiveAsFlow()

    // 抽象方法，用于初始化视图状态
    protected abstract fun initState(): State

    // 设置视图状态，使用 lambda 表达式进行状态的复制和修改
    protected fun setState(copy: State.() -> State) {
        _state.update { copy(_state.value) }
    }

    // 发送视图意图
    fun sendIntent(intent: Intent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    // 初始化代码块，用于收集意图并处理
    init {
        viewModelScope.launch {
            // 收集意图，并调用 handleIntent 方法处理
            intent.collect { intent ->
                handleIntent(intent)
            }
        }
    }
    // 抽象方法，用于处理视图意图
    protected abstract fun handleIntent(intent: Intent)
}
