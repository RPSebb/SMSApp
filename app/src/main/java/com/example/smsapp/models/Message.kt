package com.example.smsapp.models

import android.os.Parcelable
import com.example.processor.Field
import com.example.processor.Table
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Table("android.provider.Telephony.Sms.CONTENT_URI")
class Message (
    @Field("_id", "Long")       var id:       Long   = -1L,
    @Field("thread_id", "Long") val threadId: Long   = -1L,
    @Field("type", "Long")      val type:     Long   = 1L,
    @Field("date", "Long")      val date:     Long   = 1L,
    @Field("body", "String")    val body:     String = "",
    @Field("read", "Int")       var read:     Int    = 0,
    @Field("address", "String") var address:  String = "",
) : Parcelable