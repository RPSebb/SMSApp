package com.example.smsapp.models

import android.os.Parcelable
import com.example.processor.Field
import com.example.processor.Table
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Table("android.provider.Telephony.Threads.CONTENT_URI")
data class Conversation (
    @Field("_id", "Long")       var id:       Long   = -1L,
    @Field("thread_id", "Long") val threadId: Long   = -1L,
    @Field("address", "String") val address:  String = "",
    @Field("body", "String")    val body:     String = "",
    @Field("date", "Long")      val date:     Long   = 1L,
    @Field("read", "Int")       var read:     Int    = 0,
    @Field("count(*)", "Int")   var count:    Int    = 0
) : Parcelable