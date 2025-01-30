package com.example.smsapp.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smsapp.SmsApplication
import com.example.smsapp.models.Conversation
import com.example.smsapp.repositories.SmsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: SmsApplication,
    private val smsRepository: SmsRepository
) : AndroidViewModel(application) {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    init {
        viewModelScope.launch {
            fetchConversations()
            application.smsEvents.collect {
                fetchConversations()
            }
        }
    }

    suspend fun fetchConversations() {
        smsRepository.getAll<Conversation>(
            selection = "address NOT NULL AND body NOT NULL AND date NOT NULL",
            order = "date DESC")
        .collect { _conversations.value = it }
    }

}