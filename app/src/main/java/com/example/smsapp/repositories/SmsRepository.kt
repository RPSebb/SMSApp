package com.example.smsapp.repositories

import android.content.ContentResolver
import android.content.ContentValues
import android.provider.Telephony
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.models.generated.ModelFactory
import javax.inject.Inject

class SmsRepository @Inject constructor(val contentResolver: ContentResolver) {

    inline fun <reified T> get(
        selection : String? = "_id = 1",
        selectionArgs : Array<String>? = null,
        order : String? = null
    ) = flow {
        val cursor = contentResolver.query(
            ModelFactory.getTable<T>(),
            ModelFactory.getColumns<T>(),
            selection,
            selectionArgs,
            order
        )
        cursor?.use{ if(it.moveToNext()) { emit(ModelFactory.createFromCursor<T>(it)) } }
    }.flowOn(Dispatchers.IO)

    inline fun <reified T> getAll(
        selection : String? = null,
        selectionArgs : Array<String>? = null,
        order : String? = null
    ) = flow {
        val cursor = contentResolver.query(
            ModelFactory.getTable<T>(),
            ModelFactory.getColumns<T>(),
            selection,
            selectionArgs,
            order
        )
        val modelInstances = mutableListOf<T>()
        cursor?.use {
            while(it.moveToNext()) {
                modelInstances.add(ModelFactory.createFromCursor<T>(it))
            }
            emit(modelInstances)
        }
    }.flowOn(Dispatchers.IO)

    // can not set message read to true
    // god fucking damn it
    fun setMessageRead(id: Long) {
        val uri = Telephony.Sms.Inbox.CONTENT_URI
        val values = ContentValues()
        values.put("read", 1)
        values.put("seen", 1)

        val rows = contentResolver.update(
            uri,
            values,
            "_id = ?",
            arrayOf("$id")
        )

        Log.d("rpsebb", "rows updated : $rows")
    }
}