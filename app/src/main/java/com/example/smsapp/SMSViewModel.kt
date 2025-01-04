package com.example.smsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SMSViewModel @Inject constructor(private val smsRepository: SMSRepository) : ViewModel() {

    private val _threads = MutableStateFlow<Map<Long, SMSThread>>(emptyMap())
    private val _threadsOrder  = MutableStateFlow<List<Long>>(emptyList())

    private val _messages = MutableStateFlow<Map<Long, Map<Long, SMS>>>(emptyMap())
    private val _messagesOrder  = MutableStateFlow<List<Long>>(emptyList())

    val threads: StateFlow<Map<Long, SMSThread>> = _threads
    val threadsOrder: StateFlow<List<Long>> = _threadsOrder

    val messages: StateFlow<Map<Long, Map<Long, SMS>>> = _messages
    val messagesOrder: StateFlow<List<Long>> = _threadsOrder

    init {
        viewModelScope.launch {
            smsRepository.getThreads()
                .collect { fetchedThreads ->
                    _threads.value = fetchedThreads
                    _threadsOrder.value = _threads.value.keys.sortedByDescending { _threads.value[it]?.date }
                }
        }
    }

    fun getMessages(threadId: Long) {
         viewModelScope.launch {
            smsRepository.getSMS(threadId = threadId).collect { fetchedSMS ->
                val currentMessages = _messages.value[threadId] ?: emptyMap()
                _messages.value = _messages.value.toMutableMap().apply {
                    put(threadId, currentMessages + fetchedSMS)
                }
            }
         }
    }
}