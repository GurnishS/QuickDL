package com.solvynix.quickdl.ui.screens.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solvynix.quickdl.data.chaquopy.ChaquopyHandler
import com.solvynix.quickdl.data.local.VideoDao
import com.solvynix.quickdl.data.local.VideoInfo
import com.solvynix.quickdl.models.DownloadsScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DownloadsViewModel(private val dao: VideoDao) : ViewModel() {

    private val _state = MutableStateFlow(DownloadsScreenState())
    val state: StateFlow<DownloadsScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch() {
            dao.getAllVideos().collect { videos ->
                _state.value = _state.value.copy(videos = videos)
            }
        }
    }

    fun onEvent(event: DownloadsScreenEvent) {
        when (event) {
            is DownloadsScreenEvent.onDownloadClicked -> startDownload()
            is DownloadsScreenEvent.setAudioQuality -> {
                _state.update {
                    it.copy(audioQuality = event.audioQuality)
                }
            }

            is DownloadsScreenEvent.setUrl -> {
                _state.update { it.copy(inputUrl = event.inputUrl) }
            }

            is DownloadsScreenEvent.setVideoQuality -> {
                _state.update { it.copy(videoQuality = event.videoQuality) }
            }

        }
    }

    fun onProgressVideo(videoInfo: VideoInfo, progress: Int, speed: Int) {
        println("Progress: $progress, Speed: $speed KB/s") // Debug log
        _state.update { state ->
            state.copy(videos = state.videos.map {
                if (it.url == videoInfo.url) it.copy(
                    videoSpeed = speed,
                    status = "Downloading Files",
                    videoProgress = progress,
                    progress = ((progress + it.audioProgress) / 2.0).roundToInt(),
                    speed = (((speed + it.audioSpeed) / 2.0) / (1024 * 1024)).toInt().toString() + " MB/s"
                ) else it
            })
        }
    }


    fun onProgressAudio(videoInfo: VideoInfo, progress: Int, speed: Int) {
        _state.update { state ->
            state.copy(videos = state.videos.map {
                if (it.url == videoInfo.url) it.copy(
                    audioSpeed = speed,
                    status = "Downloading Files",
                    audioProgress = progress,
                    progress = ((progress + it.videoProgress) / 2.0).roundToInt(),
                    speed = (((speed + it.videoSpeed) / 2.0) / (1024 * 1024)).toInt().toString() + " MB/s"
                ) else it
            })
        }
    }


    fun onProgress(videoInfo: VideoInfo, progress: Int, speed: Int) {
        _state.update { state ->
            state.copy(videos = state.videos.map {
                if (it.url == videoInfo.url) it.copy(speed = ((speed/1024/1024).toInt()).toString()+" MB/s", status = "Downloading Files", progress = progress) else it
            })
        }
    }



    private fun startDownload() {
        val currentState = _state.value

        val placeholderVideo = VideoInfo(
            url = currentState.inputUrl,
            title = null,
            thumbnail = null, path = null, audioPath = null, videoPath = null,
            duration = null, width = null, height = null, fileSize = null,
            status = "Fetching Info", extractor = null, uploader = null,
            audioFormat = null, videoFormat = null, audioProgress = 0, videoProgress = 0, speed = "0 KB/s",
        )

        // 🔹 Immediately update UI
        _state.value = _state.value.copy(videos = _state.value.videos + placeholderVideo)

        viewModelScope.launch(Dispatchers.IO) {
            // Insert placeholder into Room
            dao.upsertVideo(placeholderVideo)

            // Fetch actual video info
            val videoMap = ChaquopyHandler.getVideoInfo(
                currentState.inputUrl, currentState.videoQuality, currentState.audioQuality
            )



            val updatedVideo = placeholderVideo.copy(
                title = videoMap["title"] as? String ?: "Unknown Title",
                thumbnail = videoMap["thumbnail"] as? String ?: "Default Thumbnail",
                duration = videoMap["duration"] as? String ?: "Unknown",
                width = videoMap["width"] as? Int ?: 0,
                height = videoMap["height"] as? Int ?: 0,
                fileSize = (videoMap["fileSize"] as? Number)?.toDouble()?.takeIf { it > 0 }?.div(1024 * 1024)?.let {
                    "%.2f MB".format(it)
                } ?: "Unknown",
                extractor = videoMap["extractor"] as? String ?: "Unknown",
                uploader = videoMap["uploader"] as? String,
                status = "Ready to Download",
                audioFormat = videoMap["audioFormat"] as? String,
                videoFormat = videoMap["videoFormat"] as? String
            )

            dao.upsertVideo(updatedVideo) // Update with real data


            if(videoMap["videoFormat"]!=null && videoMap["audioFormat"]!=null){
                val videoJob=viewModelScope.async(Dispatchers.IO) {
                    downloadFormat(updatedVideo, videoMap["videoFormat"] as String) { videoInfo, progress, speed ->
                        onProgressVideo(videoInfo, progress, speed)
                    }
                }
                val audioJob=viewModelScope.async(Dispatchers.IO) {
                    downloadFormat(updatedVideo, videoMap["audioFormat"] as String) { videoInfo, progress, speed ->
                        onProgressAudio(videoInfo, progress, speed)
                    }

                }
                viewModelScope.launch {
                    val videoPath = videoJob.await() // ✅ Get the result correctly
                    val audioPath = audioJob.await() // ✅ Get the result correctly
                    println("Video Path: $videoPath, Audio Path: $audioPath")
                    dao.upsertVideo(updatedVideo.copy(videoPath = videoPath, audioPath = audioPath, status = "Downloaded"))
                }
            }
            else{
                val job=viewModelScope.async(Dispatchers.IO) {
                    downloadFormat(updatedVideo, "") { videoInfo, progress, speed ->
                        onProgress(videoInfo, progress, speed)
                    }

                }
                viewModelScope.launch {
                    val path = job.await() // ✅ Get the result correctly
                    dao.upsertVideo(updatedVideo.copy(path=path, status = "Downloaded"))
                }
            }







        }
    }


    private suspend fun downloadFormat(videoInfo: VideoInfo, formatId: String, onProgress: (VideoInfo, Int, Int) -> Unit):String {
        return ChaquopyHandler.downloadFormat(
            url = videoInfo.url,
            formatId = formatId,
            onProgress = { progress, speed ->
                onProgress(videoInfo, progress, speed)
            }
        )
    }




}
