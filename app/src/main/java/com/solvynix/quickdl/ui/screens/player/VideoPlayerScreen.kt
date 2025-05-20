package com.solvynix.quickdl.ui.screens.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    navController: NavController,
    uri: String,
    title: String,
    videoUri: String,
    audioUri: String
) {
    val context = LocalContext.current

    // Remember and restore playback position
    var playbackPosition by rememberSaveable { mutableStateOf(0L) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaSourceFactory = DefaultMediaSourceFactory(context)

            val mediaSource = if (uri.isNotEmpty()) {
                // Play both video and audio from the same URI
                mediaSourceFactory.createMediaSource(MediaItem.fromUri(uri.toUri()))
            } else {
                // Play video and audio from separate URIs
                val videoSource = mediaSourceFactory.createMediaSource(MediaItem.fromUri(videoUri.toUri()))
                val audioSource = mediaSourceFactory.createMediaSource(MediaItem.fromUri(audioUri.toUri()))

                // Merge video and audio sources
                MergingMediaSource(videoSource, audioSource)
            }

            setMediaSource(mediaSource)
            seekTo(playbackPosition) // Restore previous position
            prepare()
            playWhenReady = true
        }
    }

    // ExoPlayer UI inside Compose
    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    // Save position and release player when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            playbackPosition = exoPlayer.currentPosition // Save current position
            exoPlayer.release()
        }
    }
}

@Preview
@Composable
fun VideoPlayerScreenPreview() {
    VideoPlayerScreen(
        navController = NavController(LocalContext.current),
        "https://www.youtube.com/watch?v=6JYIGclVQdw",
        "Title",
        "VideoUri",
        "AudioUri"
    )
}
