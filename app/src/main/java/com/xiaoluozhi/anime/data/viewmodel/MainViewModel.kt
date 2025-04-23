package com.xiaoluozhi.anime.data.viewmodel

import com.xiaoluozhi.anime.ui.activity.Screen

data class MainState(
    val title: String,
) : BaseState

sealed class MainIntent : BaseIntent {
    data class UpdateTitle(val newTitle: String) : MainIntent()
}

class MainViewModel : BaseViewModel<MainState, MainIntent>() {
    // 设置初始状态，初始标题为 "首页"
    override fun initState(): MainState = MainState(Screen.Home.label) // 使用 Screen.Home.label 保证一致

    // 处理接收到的 Intent
    override fun handleIntent(intent: MainIntent) {
        when (intent) {
            // 当收到 UpdateTitle Intent 时...
            is MainIntent.UpdateTitle -> {
                // 使用 BaseViewModel 提供的 setState 方法更新状态
                setState {
                    // 创建当前状态的一个副本，只修改 title 字段
                    copy(title = intent.newTitle)
                }
            }
        }
    }
}