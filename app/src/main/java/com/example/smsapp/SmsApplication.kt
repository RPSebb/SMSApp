package com.example.smsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@HiltAndroidApp
class SmsApplication : Application() {

    internal val applicationScope = CoroutineScope(Dispatchers.Main)
    private val events = MutableSharedFlow<Int>()
    val smsEvents: SharedFlow<Int> = events

    suspend fun emitSMSEvent(type: Int) {
        events.emit(type)
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}

