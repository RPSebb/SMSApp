package com.example.smsapp

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext

class DbUtils (private val contentResolver: ContentResolver) {

    fun Cursor.getValue(columnName: String): Any? {
        val columnIndex = getColumnIndex(columnName)
        val type = getType(columnIndex)

        return when (type) {
            Cursor.FIELD_TYPE_STRING -> getString(columnIndex)
//        Cursor.FIELD_TYPE_INTEGER -> getInt(columnIndex)
            Cursor.FIELD_TYPE_INTEGER -> getLong(columnIndex)
            Cursor.FIELD_TYPE_FLOAT -> getFloat(columnIndex)
//        Cursor.FIELD_TYPE_FLOAT -> getDouble(columnIndex)
            else -> null
        }
    }

    fun showColumns(uri: Uri) {
        val cursor = contentResolver.query(uri, null, null, null, null)
        Log.d("RPSebb", uri.toString())
        cursor?.use {
            Log.d("RPSebb", it.columnNames.joinToString(", "))
        }
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    suspend fun query(
        uri: Uri,
        projection: Array<String>? = null,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        order: String? = null,
        limit: Int = Int.MAX_VALUE,
        offset: Int = 0
    ) {
        withContext(Dispatchers.IO) {

            val cursor = contentResolver.query(uri, projection, selection, selectionArgs,
                "$order LIMIT $limit OFFSET $offset"
            )

            cursor?.use {
                var line = ""
                for (column in it.columnNames) { line += column.take(20).padEnd(20, ' ') + " | " }
                Log.d("RPSebb", line)

                while(it.moveToNext()) {
                    line = ""
                    for(column in it.columnNames) {
                        val value: String = it.getValue(column).toString()
                            .replace("\n", "")
                            .replace("\r", "")
                            .replace(Regex("[\\p{So}\\p{C}]+"), "\uFFFD")
                            .take(20)
                            .padEnd(20, ' ')
                        line += "$value | "
                    }
                    Log.d("RPSebb", line)
                }
            }
        }
    }
}