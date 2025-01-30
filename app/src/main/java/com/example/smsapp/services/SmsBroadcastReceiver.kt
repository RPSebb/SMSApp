package com.example.smsapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import com.example.smsapp.SmsApplication
import com.example.smsapp.models.Conversation
import com.example.smsapp.models.Message
import com.example.smsapp.repositories.SmsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SmsBroadcastReceiver() : BroadcastReceiver() {

    val tag = this::class.simpleName

    suspend fun onSmsReceive(context: Context, intent: Intent) {

        val application = context.applicationContext as SmsApplication
        val notificationTime = System.currentTimeMillis()
        var messages: Array<SmsMessage> = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        // wait 5 sec for insertion in database
        // retrieve can fail if insertion in db took too much time
        // then insertion date will be > dateMax
        delay(5000)
        for(message in messages) {
            val address = message.displayOriginatingAddress
            val body = message.displayMessageBody
            val smsRepository = SmsRepository(context.contentResolver)

            smsRepository.get<Message>(
                "date > ? AND date < ? AND address = ?",
                arrayOf("${notificationTime - 5000}", "$notificationTime", "$address"),
                "date DESC LIMIT 1"
            ).collect { smsMessage ->

                // Forced to inject value in selection instead of using selectionArgs
                // Cuz this shitty cursor give fucking wrong results
                // I don't understand this fucking shit, can't anything work properly in android ?
                // Update the fucking documentation with examples
                // Maybe it is because it a "build" query with multiple tables
                // Anyway, I don't have control over this
                smsRepository.get<Conversation>("thread_id = ${smsMessage.threadId}")
                .collect { NotificationService(context).notifyMessage(address, body, it) }
            }

            application.emitSMSEvent(0)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val application = context?.applicationContext as SmsApplication
        val action = intent?.action

        application.applicationScope.launch {
             when(action) {
                 "android.provider.Telephony.SMS_RECEIVED" ->
                     try { onSmsReceive(context, intent) } catch(e: Exception) { Log.d(tag, e.message.toString())}
                 "SMS_SENT" -> application.emitSMSEvent(1)
                 "SMS_DELIVER" -> application.emitSMSEvent(2)
                 else -> Log.d(tag, "unknown action")
            }
        }
    }
}