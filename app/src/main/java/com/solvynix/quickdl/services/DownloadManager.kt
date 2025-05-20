package com.solvynix.quickdl.services

import android.content.Context
import com.solvynix.quickdl.data.local.VideoDao
import com.solvynix.quickdl.ui.screens.downloads.DownloadsScreenEvent
import com.solvynix.quickdl.ui.screens.downloads.DownloadsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object DownloadManager {
    lateinit var dao: VideoDao

    fun init(videoDao: VideoDao) {
        dao = videoDao
    }

    fun startDownloadFromService(context: Context, url: String, videoQuality: Int, audioQuality: String) {
        val viewModel = DownloadsViewModel(dao)

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.onEvent(DownloadsScreenEvent.setUrl(url))
            viewModel.onEvent(DownloadsScreenEvent.setVideoQuality(videoQuality))
            viewModel.onEvent(DownloadsScreenEvent.setAudioQuality(audioQuality))
            viewModel.onEvent(DownloadsScreenEvent.onDownloadClicked(context))
        }

    }
}
