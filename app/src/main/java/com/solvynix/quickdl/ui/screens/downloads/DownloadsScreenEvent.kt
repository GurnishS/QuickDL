package com.solvynix.quickdl.ui.screens.downloads

sealed interface DownloadsScreenEvent{
    object onDownloadClicked:DownloadsScreenEvent
    data class setUrl(val inputUrl:String):DownloadsScreenEvent
    data class setVideoQuality(val videoQuality:Int):DownloadsScreenEvent
    data class setAudioQuality(val audioQuality:String):DownloadsScreenEvent
}