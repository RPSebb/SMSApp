package com.example.smsapp

import android.content.ContentResolver
import android.provider.Telephony
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SMSRepository @Inject constructor(private val contentResolver: ContentResolver) {

    fun getThreads(): Flow<Map<Long, SMSThread>> = flow {
        val uri = Telephony.Threads.CONTENT_URI
        val projection = arrayOf("_id", "thread_id", "address", "body", "date")
        val selection = "address NOT NULL AND body NOT NULL AND date NOT NULL"
        val order = "date DESC"

        val cursor = contentResolver.query(uri, projection, selection, null, order)

        cursor?.use {
            val threads = mutableMapOf<Long, SMSThread>()
            while (cursor.moveToNext()) {
                threads[it.getLong(1)] = SMSThread(it.getLong(0), it.getLong(1), it.getString(2), it.getString(3), it.getLong(4))
            }
            emit(threads)
        }
    }

    fun getSMS(threadId: Long, limit: Int = 20, offset: Int = 0): Flow<Map<Long, SMS>> = flow {
        val uri = Telephony.Sms.CONTENT_URI
        val projection = arrayOf<String>("_id", "thread_id", "type", "date", "body")
        val selection = "thread_id = ?"
        val selectionArgs = arrayOf<String>("$threadId")
        val order = "date DESC LIMIT $limit OFFSET $offset"

        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, order)

        cursor?.use {
            val messages = mutableMapOf<Long, SMS>()
            while(cursor.moveToNext()) {
                messages[it.getLong(0)] = SMS(it.getLong(0), it.getLong(1), it.getLong(2), it.getLong(3), it.getString(4))
            }
            emit(messages)
        }
    }
}