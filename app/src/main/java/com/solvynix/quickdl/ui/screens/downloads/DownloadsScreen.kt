package com.solvynix.quickdl.ui.screens.downloads

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.solvynix.quickdl.models.DownloadsScreenState
import com.solvynix.quickdl.ui.components.SearchBar
import com.solvynix.quickdl.ui.components.VideoGrid
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.res.painterResource
import com.solvynix.quickdl.R
import com.solvynix.quickdl.data.local.VideoInfo
import com.solvynix.quickdl.data.sharedprefs.PreferencesManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    navController: NavController,
    state: DownloadsScreenState,
    onEvent: (DownloadsScreenEvent) -> Unit,
    preferencesManager: PreferencesManager,
    applicationContext: Context
) {

    val onDelete: (VideoInfo) -> Unit = { videoInfo ->
        onEvent(DownloadsScreenEvent.deleteVideo(videoInfo))
    }

    if(preferencesManager.getSetting("VideoQuality")==""){
        preferencesManager.saveSetting("VideoQuality","1440")
    }
    if(preferencesManager.getSetting("AudioQuality")==""){
        preferencesManager.saveSetting("AudioQuality","High")
    }

    onEvent(DownloadsScreenEvent.setVideoQuality(preferencesManager.getSetting("VideoQuality").toInt()))
    onEvent(DownloadsScreenEvent.setAudioQuality(preferencesManager.getSetting("AudioQuality")))

    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column() {
                SearchBar(state, onEvent)
//                Button(onClick = {
//                    Intent(applicationContext, DownloadService::class.java).also {
//                        it.action = DownloadService.Actions.START.toString()
//                        ContextCompat.startForegroundService(applicationContext, it)
//                    }
//                }) {
//                    Text("Run")
//                }
//
//                Button(onClick = {
//                    Intent(applicationContext, DownloadService::class.java).also {
//                        it.action = DownloadService.Actions.STOP.toString()
//                        ContextCompat.startForegroundService(applicationContext, it)
//                    }
//
//
//                }) {
//                    Text("Stop")
//                }

                if(state.videos.isEmpty()){
                    Column(modifier=Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(R.drawable.nothing_here), contentDescription = "Nothing Here Image",modifier=Modifier.height(200.dp))
                        Text("Nothing Here 😒",Modifier.padding(bottom = 100.dp))
                    }
                }
                VideoGrid(
                    videos = state.videos,
                    navController = navController,
                    onDelete=onDelete,
                    modifier = Modifier
                )
            }

            // Arrow button at bottom center
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 5.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                IconButton(
                    onClick = {
                        isBottomSheetVisible = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Open Bottom Sheet"
                    )
                }
            }
        }

        // Actual Bottom Sheet
        if (isBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { isBottomSheetVisible = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .width(400.dp)
                    ) {
                        Column {
                            // Audio Quality Grid
                            Text(
                                "Set Audio Quality",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3), // 3 items per row
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(listOf("High", "Medium", "Low")) { quality ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        RadioButton(
                                            selected = state.audioQuality == quality,
                                            onClick = {
                                                onEvent(
                                                    DownloadsScreenEvent.setAudioQuality(
                                                        quality
                                                    )
                                                )
                                                preferencesManager.saveSetting("AudioQuality",quality)
                                            }
                                        )
                                        Text(
                                            text = quality,
                                        )
                                    }
                                }
                            }
                            Text(
                                "Set Video Quality",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3), // 3 items per row
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(listOf(2160, 1440, 1080, 720, 480, 360)) { quality ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        RadioButton(
                                            selected = state.videoQuality == quality,
                                            onClick = {
                                                onEvent(
                                                    DownloadsScreenEvent.setVideoQuality(
                                                        quality
                                                    )
                                                )
                                                preferencesManager.saveSetting("VideoQuality",quality.toString())
                                            }
                                        )
                                        Text(
                                            text = when (quality) {
                                                2160 -> "4K"
                                                1440 -> "2K"
                                                1080 -> "1080p"
                                                720 -> "720p"
                                                480 -> "480p"
                                                else -> "360p"
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
