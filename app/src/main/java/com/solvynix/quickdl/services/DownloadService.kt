package com.solvynix.quickdl.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.solvynix.quickdl.RunningApp
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.solvynix.quickdl.R
import com.solvynix.quickdl.data.chaquopy.ChaquopyHandler
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class DownloadService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString()-> stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun  start(){
        val notification=NotificationCompat.Builder(this,"downloading_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("Downloading Video")
            .build()
        startForeground(1,notification)

        DownloadManager.startDownloadFromService(
            context = this,
            url = "https://www.youtube.com/watch?v=Hbtue1RQIo4",
            videoQuality = 2160,
            audioQuality = "High"
        )
    }

    enum class Actions{
        START,STOP
    }

}
