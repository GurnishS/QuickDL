package com.solvynix.quickdl

import kotlinx.serialization.Serializable

@Serializable
object DownloadsScreenDest

@Serializable
data class VideoPlayerScreenDest(val uri: String,val title:String,val videoUri:String,val audioUri:String)
