package com.example.smsapp.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.telephony.SmsManager
import com.example.smsapp.SmsApplication
import com.example.smsapp.services.SmsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }

    @Provides
    @Singleton
    fun provideContext(application: Application) : Context {
        return application.applicationContext
    }

    @Provides
    fun provideSMSApplication(application: Application): SmsApplication {
        return application as SmsApplication
    }

    @Provides
    fun provideSMSManager(application: Application): SmsManager {
        return application.applicationContext.getSystemService(SmsManager::class.java) as SmsManager
    }

    @Provides
    @Singleton
    fun provideSMSService(application: Application) : SmsService {
        return SmsService(application.applicationContext)
    }

}