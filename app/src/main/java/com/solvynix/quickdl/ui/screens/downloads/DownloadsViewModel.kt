package com.solvynix.quickdl.ui.screens.downloads

import android.content.Context
import android.util.Log
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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt
import android.content.SharedPreferences


class DownloadsViewModel(private val dao: VideoDao) : ViewModel() {

    private val _state = MutableStateFlow(DownloadsScreenState())
    val state: StateFlow<DownloadsScreenState> = _state.asStateFlow()

    init {
        println("View Model Initialised")
        viewModelScope.launch() {
            dao.getAllVideos().collect { videos ->
                _state.value = _state.value.copy(videos = videos)
            }
        }
    }

    fun onEvent(event: DownloadsScreenEvent) {
        when (event) {
            is DownloadsScreenEvent.onDownloadClicked ->startDownload(event.context)

            is DownloadsScreenEvent.setAudioQuality -> {
                _state.update {
                    it.copy(audioQuality = event.audioQuality)
                }
            }

            is DownloadsScreenEvent.deleteVideo-> deleteFiles(event.videoInfo)

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



    private fun startDownload(context: Context) {
        val currentState = _state.value

        val placeholderVideo = VideoInfo(
            url = currentState.inputUrl,
            title = null,
            thumbnail = null, path = null, audioPath = null, videoPath = null,
            duration = null, width = null, height = null, fileSize = null,
            status = "Fetching Info", extractor = null, uploader = null,
            audioFormat = null, videoFormat = null, audioProgress = 0, videoProgress = 0, speed = "0 KB/s",
            thumbnailPath = null
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

            if(!updatedVideo.thumbnail.isNullOrBlank()){
                val path=downloadThumbnail(context,updatedVideo.thumbnail,updatedVideo.thumbnail)
                updatedVideo.thumbnailPath=path;
                dao.upsertVideo(updatedVideo)
            }


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

    private fun deleteFiles(videoInfo: VideoInfo) {
        videoInfo.audioPath?.let { audioPath ->
            viewModelScope.launch(Dispatchers.IO) {
                deleteFile(audioPath, "Audio")
            }
        }

        videoInfo.videoPath?.let { videoPath ->
            viewModelScope.launch(Dispatchers.IO) {
                deleteFile(videoPath, "Video")
            }
        }

        videoInfo.path?.let { additionalPath ->
            viewModelScope.launch(Dispatchers.IO) {
                deleteFile(additionalPath, "Path")
            }
        }

        viewModelScope.launch {
            dao.deleteVideo(videoInfo)
        }
    }

    private fun deleteFile(filePath: String, fileType: String) {
        val file = File(filePath)
        if (file.exists()) {
            try {
                if (file.delete()) {
                    Log.d("$fileType File Delete", "File deleted successfully: $filePath")
                } else {
                    Log.d("$fileType File Delete", "Failed to delete $fileType file: $filePath")
                }
            } catch (e: Exception) {
                Log.e("$fileType File Delete", "Error deleting $fileType file: $filePath", e)
            }
        } else {
            Log.d("$fileType File Delete", "$fileType file does not exist: $filePath")
        }
    }


    fun downloadThumbnail(context: Context, urlString: String, fileName: String): String? {
        try {
            // Sanitize the URL to create a valid filename
            val sanitizedFileName = sanitizeUrl(urlString)

            // Create the 'thumbnails' folder inside internal storage if it doesn't exist
            val thumbnailsDir = File(context.filesDir, "thumbnails")
            if (!thumbnailsDir.exists()) {
                thumbnailsDir.mkdirs()
            }

            // Create the final file inside thumbnails with the sanitized file name
            val outputFile = File(thumbnailsDir, sanitizedFileName)

            // Start downloading
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()

            val inputStream: InputStream = connection.inputStream
            val outputStream = FileOutputStream(outputFile)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.close()
            inputStream.close()

            println("Image saved at: ${outputFile.absolutePath}")
            return outputFile.absolutePath
        } catch (e: Exception) {
            Log.e("Thumbnail Download Error", "Error downloading thumbnail: ${e.message}", e)
        }
        return null
    }

    // Function to sanitize the URL and create a valid filename
    fun sanitizeUrl(url: String): String {
        return url.hashCode().toString() // Using hashcode of URL as the filename
    }



}
