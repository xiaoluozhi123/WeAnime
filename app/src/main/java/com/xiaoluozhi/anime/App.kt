package com.xiaoluozhi.anime

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import com.tencent.mmkv.MMKV

class App : Application(){
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var kv: MMKV
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        // 初始化MMKV
        MMKV.initialize(this)
        kv = MMKV.mmkvWithID("kv", MMKV.MULTI_PROCESS_MODE)
    }
}

@Composable
fun AppNavHost() {

}