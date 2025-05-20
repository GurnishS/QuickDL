package com.solvynix.quickdl.ui.screens.downloads

import android.content.Context
import com.solvynix.quickdl.data.local.VideoInfo

sealed interface DownloadsScreenEvent{
    data class onDownloadClicked(val context: Context):DownloadsScreenEvent
    data class deleteVideo(val videoInfo: VideoInfo):DownloadsScreenEvent
    data class setUrl(val inputUrl:String):DownloadsScreenEvent
    data class setVideoQuality(val videoQuality:Int):DownloadsScreenEvent
    data class setAudioQuality(val audioQuality:String):DownloadsScreenEvent
}