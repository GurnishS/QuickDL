package com.solvynix.quickdl

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.solvynix.quickdl.data.local.VideoDatabase
import com.solvynix.quickdl.services.DownloadManager

class RunningApp:Application() {
    override fun onCreate() {
        super.onCreate()
        DownloadManager.init(VideoDatabase.getInstance(this).videoDao)

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel=NotificationChannel(
                "downloading_channel",
                "Running Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}