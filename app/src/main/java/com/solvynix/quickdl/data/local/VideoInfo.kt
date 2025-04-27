package com.solvynix.quickdl.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoInfo(
    val title: String?,
    @PrimaryKey
    val url: String,
    val path: String?,
    val videoPath: String?,
    val audioPath: String?,
    val thumbnail: String?,
    val duration: String?,
    val width: Int?,
    val height: Int?,
    val fileSize: String?,
    val status: String?,
    val extractor: String?,
    val uploader: String?,
    val audioFormat:String?,
    val videoFormat:String?,
    val audioProgress:Int=0,
    val videoProgress:Int=0,
    val progress:Int=0,
    val videoSpeed:Int=0,
    val audioSpeed:Int=0,
    val speed:String="0 KB/s",
//    val id: Int=0,
)
