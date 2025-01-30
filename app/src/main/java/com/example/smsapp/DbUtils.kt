package com.example.smsapp

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
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

    fun columnTypeToString(type: Int): String {
        return when(type) {
            Cursor.FIELD_TYPE_STRING  -> "string"
            Cursor.FIELD_TYPE_BLOB    -> "blob"
            Cursor.FIELD_TYPE_NULL    -> "null"
            Cursor.FIELD_TYPE_INTEGER -> "int"
            Cursor.FIELD_TYPE_FLOAT   -> "float"
            else -> "unknown"
        }
    }

    fun showColumnsType(uri: Uri) {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if(it.moveToFirst()) {
                for (column in it.columnNames) {
                    val index = it.getColumnIndex(column)
                    val type  = it.getType(index)
                    val typeName = columnTypeToString(type)
                    Log.d("RPSebb", "$column : $typeName")
                }
            } else {
                Log.d("RPSebb", "Empty Cursor")
            }
        }
    }

    @SuppressLint("Range")
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

    fun update(uri: Uri, id: Long, values: Map<String, String>) {

        val contentValues = ContentValues()
        values.forEach { key, value ->
            contentValues.put(key, value)
        }

        val rows = contentResolver.update(uri, contentValues, "_id = ?", arrayOf("$id"))
        Log.d("rpsebb", "$rows updated")

//        this.query(uri = uri, selection = "_id = ?", selectionArgs = arrayOf("$id"), limit = 1)
    }

    fun delete(uri: Uri, selection: String? = null, selectionArgs: Array<String>? = null) {

        val rows = contentResolver.delete(uri, selection, selectionArgs)
        Log.d("rpsebb", "$rows deleted")
    }
}