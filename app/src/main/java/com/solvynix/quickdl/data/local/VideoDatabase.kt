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

    companion object {
        @Volatile
        private var INSTANCE: VideoDatabase? = null

        fun getInstance(context: Context): VideoDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VideoDatabase::class.java,
                    "quickdl_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}