//package com.solvynix.quickdl.ui.screens
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.media.MediaMetadataRetriever
//import android.util.Log
//import android.widget.Toast
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateMapOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.graphics.painter.BitmapPainter
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalClipboardManager
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.compose.LocalLifecycleOwner
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.NavController
//import com.solvynix.quickdl.R
//import com.solvynix.quickdl.VideoPlayerScreenDest
//import com.solvynix.quickdl.utils.ChaquopyHandler
//import com.solvynix.quickdl.utils.DownloadHandler
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.File
//import kotlin.math.ceil
//
//@Composable
//fun InputBox(
//    downloadingMap: MutableMap<String, VideoDetail>,
//    modifier: Modifier = Modifier
//) {
//    var url by rememberSaveable { mutableStateOf("") }
//    val clipboardManager = LocalClipboardManager.current
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val downloadHandler = remember { DownloadHandler(context) }
//    val downloadStatus by downloadHandler.downloadStatus.observeAsState()
//
//    fun onSearch(context: Context, url: String, lifecycleOwner: LifecycleOwner) {
//        val downloadHandler = DownloadHandler(context)
//
//
//        // Observe LiveData using lifecycleOwner
//        downloadHandler.downloadStatus.observe(lifecycleOwner) { status ->
//            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
//        }
//
//        lifecycleOwner.lifecycleScope.launch {
//            val filePath = downloadHandler.downloadVideo(url, 240,downloadingMap)
//            Log.d("Downloaded File", filePath ?: "Failed")
//        }
//    }
//
//
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(60.dp)
//            .padding(horizontal = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        TextField(
//            shape = RoundedCornerShape(8.dp),
//            value = url,
//            onValueChange = { url = it },
//            placeholder = { Text("Enter URL", fontSize = 16.sp) },
//            textStyle = TextStyle(fontSize = 16.sp, lineHeight = 16.sp),
//            leadingIcon = {
//                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
//            },
//            visualTransformation = VisualTransformation.None,
//            trailingIcon = {
//                IconButton(onClick = {
//                    clipboardManager.getText()?.let {
//                        url = it.text
//                        Toast.makeText(context, "Pasted from clipboard", Toast.LENGTH_SHORT).show()
//                    }
//                }) {
//                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Paste")
//                }
//            },
//            modifier = Modifier
//                .weight(1f)
//                .height(50.dp)
//                .padding(end = 8.dp),
//            singleLine = true,
//            colors = TextFieldDefaults.colors(
////                focusedContainerColor = Color.Transparent,  // Removes background when focused
////                unfocusedContainerColor = Color.Transparent,  // Removes background when unfocused
//                focusedIndicatorColor = Color.Transparent,  // Removes bottom line when focused
//                unfocusedIndicatorColor = Color.Transparent,
//            )
//        )
//        Button(
//            modifier = Modifier
//                .weight(0.4f)
//                .height(50.dp),
//
//            onClick = { onSearch(context, url, lifecycleOwner) }, // Pass lifecycleOwner
////            enabled = url.isNotBlank()
//        ) {
//            Text("Search")
//        }
//        downloadStatus?.let { status ->
//            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
//        }
//    }
//}
//
//fun getVideoThumbnail(context: Context, videoPath: String): Bitmap? {
//    val retriever = MediaMetadataRetriever()
//    return try {
//        retriever.setDataSource(videoPath)
//
//        val frame = retriever.getFrameAtTime(1_000_000, MediaMetadataRetriever.OPTION_CLOSEST)
//            ?: retriever.getFrameAtTime(500_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
//            ?: retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
//
//        frame ?: throw IllegalStateException("Failed to extract frame from video.")
//    } catch (e: Exception) {
//        Log.e("Thumbnail Problem", "Error extracting thumbnail: ${e.message}")
//        null
//    } finally {
//        try {
//            retriever.release()
//        } catch (e: Exception) {
//            Log.e("Thumbnail Problem", "Failed to release retriever: ${e.message}")
//        }
//    }
//}
//
//
//fun getFileSize(file: File): String {
//    return try {
//        if (!file.exists()) return "Unknown (File Not Found)"
//
//        val bytes = file.length()
//        val kb = bytes / 1024.0
//        val mb = kb / 1024.0
//        val gb = mb / 1024.0
//
//        when {
//            gb >= 1 -> String.format("%.2f GB", gb)
//            mb >= 1 -> String.format("%.2f MB", mb)
//            kb >= 1 -> String.format("%.2f KB", kb)
//            else -> "$bytes Bytes"
//        }
//    } catch (e: Exception) {
//        Log.e("File Size", "Error: $e")
//        "Unknown"
//    }
//}
//
//fun getVideoDuration(filePath: String): Long {
//    val retriever = MediaMetadataRetriever()
//    return try {
//        retriever.setDataSource(filePath)
//        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//        durationStr?.toLong() ?: 0L
//    } catch (e: Exception) {
//        e.printStackTrace()
//        0L
//    } finally {
//        retriever.release()
//    }
//}
//
//fun getVideoResolution(filePath: String): String {
//    val retriever = MediaMetadataRetriever()
//    return try {
//        retriever.setDataSource(filePath)
//        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
//        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
//        if (width > 0 && height > 0) "$width x $height" else "Unknown Resolution"
//    } catch (e: Exception) {
//        e.printStackTrace()
//        "Unknown Resolution"
//    } finally {
//        retriever.release()
//    }
//}
//
//fun getFilesInDirectory(context: Context, path: String?): List<VideoDetail> {
//    try {
//        if (path == null) return emptyList()
//
//        val returnList = mutableListOf<VideoDetail>()
//        val dir = File(path)
//        val fileList = dir.list()?.toList() ?: emptyList()
//
//        for (s in fileList) {
//            val title = s.substringBeforeLast(".")
//            val filePath = "$path/$s"
//            val size = getFileSize(File(filePath))
//
//            val thumbnail = getVideoThumbnail(context, filePath)
//                ?: BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background) // Fallback Image
//
//            val duration = getVideoDuration(filePath)
//            val resolution = getVideoResolution(filePath)
//            val status = "Downloaded"
//            val videoStatus = 100.0
//            val audioStatus = 100.0
//            val intSeconds= ceil(duration/1000.0).toInt()
//            returnList.add(
//                VideoDetail(
//                    title, "${intSeconds/60}:${intSeconds-(intSeconds/60)*60}", size, thumbnail, resolution.toString(),
//                    status, videoStatus, audioStatus, filePath
//                )
//            )
//        }
//        return returnList
//    } catch (e: Exception) {
//        Log.e("Files in Directory", "Error: ${e.message}")
//        return emptyList()
//    }
//}
//
//
//data class VideoDetail(
//    var title: String,
//    var duration: String,
//    var size: String,
//    var thumbnail: Bitmap,
//    var resolution: String,
//    var status: String,
//    var videoStatus:Double,
//    var audioStatus:Double,
//    var path: String,
//){
//
//}
//
//
//@Composable
//fun DownloadsScreen(navController: NavController) {
//
//    var mergedLocation by rememberSaveable { mutableStateOf<String?>(null) }
//    val downloadingMap = remember { mutableStateMapOf<String, VideoDetail>() }
//    var filesList by rememberSaveable { mutableStateOf<List<VideoDetail>>(emptyList()) }
//    var isLoading by rememberSaveable { mutableStateOf(true) }
//
//    val context = LocalContext.current
//    LaunchedEffect(Unit) {
//        withContext(Dispatchers.IO) {
//            val location = ChaquopyHandler.getMergedPath(context)
//            mergedLocation = location
//            Log.d("Merged Location", mergedLocation!!)
//            filesList = getFilesInDirectory(context,location)
//            isLoading = false
//        }
//    }
//
//    Scaffold(
//        topBar = { InputBox(downloadingMap,Modifier.statusBarsPadding()) },
//
//        ) { innerPadding ->
//        if (isLoading) {
//            Box(modifier = Modifier.fillMaxSize())
//            {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding)
//            ) {
//
//                items(downloadingMap.entries.toList()) { (key, vid) ->
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp)
//                            .clickable {
//                                navController.navigate(VideoPlayerScreenDest(uri = vid.path))
//                            }
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .weight(1f)
//                                .background(Color.Red)
//                                .height(100.dp),
//                        ) {
//                            val thumbnail: Bitmap? = vid.thumbnail
//                            thumbnail?.let {
//                                Image(
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentScale = ContentScale.Crop,
//                                    painter = BitmapPainter(it.asImageBitmap()),
//                                    contentDescription = "Video Thumbnail"
//                                )
//
//
//
//                            }
//                            CircularProgressIndicator(
//                                color = Color.Green,
//                                progress = { if(vid.status=="Downloading video...") vid.videoStatus.toFloat() else if(vid.status=="Downloading audio...") vid.audioStatus.toFloat() else 0f},
//                                modifier = Modifier.align(Alignment.Center),
//                            )
//                        }
//                        Spacer(Modifier.width(8.dp))
//                        Column(
//                            modifier = Modifier
//                                .weight(2f)
//                                .height(100.dp),
//                            verticalArrangement = Arrangement.SpaceAround
//                        ) {
//                            Text(vid.title)
//                            Row(
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                Text(vid.duration)
//                                Text(vid.size)
//                            }
//                        }
//                    }
//                }
//
//                items(filesList) { video ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp)
//                            .clickable {
//                                navController.navigate(VideoPlayerScreenDest(uri = video.path))
//                            }
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .weight(1f)
//                                .background(Color.Red)
//                                .height(100.dp),
//                        ) {
//                            val thumbnail: Bitmap? = video.thumbnail
//                            thumbnail?.let {
//                                Image(
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentScale = ContentScale.Crop,
//                                    painter = BitmapPainter(it.asImageBitmap()),
//                                    contentDescription = "Video Thumbnail"
//                                )
//                            }
//                        }
//                        Spacer(Modifier.width(8.dp))
//                        Column(
//                            modifier = Modifier
//                                .weight(2f)
//                                .height(100.dp),
//                            verticalArrangement = Arrangement.SpaceAround
//                        ) {
//                            Text(video.title)
//                            Row(
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                Text(video.duration)
//                                Text(video.size)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DownloadsScreenPreview() {
//    DownloadsScreen(navController = NavController(LocalContext.current))
//}