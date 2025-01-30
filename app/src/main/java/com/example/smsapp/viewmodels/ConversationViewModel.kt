package com.example.smsapp.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.smsapp.SmsApplication
import com.example.smsapp.models.Conversation
import com.example.smsapp.models.Message
import com.example.smsapp.repositories.SmsRepository
import com.example.smsapp.services.SmsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    savedStateHandle         : SavedStateHandle,
    private val application  : SmsApplication,
    private val smsService   : SmsService,
    private val smsRepository: SmsRepository
) : AndroidViewModel(application) {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages : StateFlow<List<Message>> = _messages

    private val _conversation = MutableStateFlow<Conversation>(Conversation())
    val conversation : StateFlow<Conversation> = _conversation

    init {
        _conversation.value = savedStateHandle.toRoute<Conversation>()
        viewModelScope.launch {
            getMessages(conversation.value.threadId)
            application.smsEvents.collect { event ->
                getMessages(conversation.value.threadId)
            }
        }
    }

    suspend fun getMessages(threadId: Long) {
        smsRepository.getAll<Message>(
            "thread_id = ?",
            arrayOf("$threadId"),
            "date DESC")
        .collect { _messages.value = it }
    }

    fun setMessageRead(id: Long) {
        viewModelScope.launch { smsRepository.setMessageRead(id) }
    }

    fun sendMessage(text : String) : Result<Unit> {
        return smsService.send(text = text, address = conversation.value.address)
    }
}