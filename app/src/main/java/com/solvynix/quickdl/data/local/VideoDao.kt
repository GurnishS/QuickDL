package com.solvynix.quickdl.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Upsert()
    suspend fun upsertVideo(videoInfo: VideoInfo)

    @Delete
    suspend fun deleteVideo(videoInfo: VideoInfo)

    @Query("SELECT * FROM videos")
    fun getAllVideos(): Flow<List<VideoInfo>>

    @Query("SELECT * FROM videos WHERE url = :url")
    suspend fun getVideoByUrl(url: String): VideoInfo?
}