package com.example.smsapp.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

class SmsService(applicationContext: Context) {

    private val smsManager : SmsManager = applicationContext.getSystemService(SmsManager::class.java) as SmsManager
    private val intent = Intent(applicationContext, SmsBroadcastReceiver::class.java)
    private val sentPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        0,
        intent.setAction("SMS_SENT"),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private val deliveryPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        0,
        intent.setAction("SMS_DELIVER"),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    fun send(text : String, address: String) : Result<Unit> {

        return try {
            if(text.isEmpty()) { throw Exception("Message is empty") }
            smsManager.sendTextMessage(address, null, text, sentPendingIntent, deliveryPendingIntent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}