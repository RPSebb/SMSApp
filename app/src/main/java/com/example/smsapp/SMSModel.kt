package com.example.smsapp

data class SMSThread(var id: Long, var threadId: Long, var address: String, var body: String, var date: Long)
data class SMS(var id: Long, var threadId: Long, var type: Long, var date: Long, var body: String)