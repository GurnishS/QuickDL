package com.solvynix.quickdl.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [VideoInfo::class],
    version = 1,
)
abstract class VideoDatabase: RoomDatabase() {

    abstract val videoDao: VideoDao
}