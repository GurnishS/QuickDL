package com.solvynix.quickdl.models

import com.solvynix.quickdl.data.local.VideoInfo

data class DownloadsScreenState(
    val videos:List<VideoInfo> = emptyList(),
    val inputUrl:String = "",
    val videoQuality:Int=2160,
    val audioQuality:String="High"
)