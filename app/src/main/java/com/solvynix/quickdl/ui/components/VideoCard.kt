package com.solvynix.quickdl.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solvynix.quickdl.R
import com.solvynix.quickdl.data.local.VideoInfo
import com.solvynix.quickdl.models.CardSizes
import com.solvynix.quickdl.ui.theme.QuickDLTheme
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.solvynix.quickdl.VideoPlayerScreenDest
import com.solvynix.quickdl.ui.screens.downloads.DownloadsViewModel
import kotlinx.coroutines.launch


@Composable
fun VideoCard(videoDetails: VideoInfo, cardSizes: CardSizes, navController: NavController,onDelete: (VideoInfo) -> Unit) {

    var isDeleteVisible by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(0f) }
    val boxWidth by animateDpAsState(
        targetValue = if (isDeleteVisible) 40.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val scope = rememberCoroutineScope()

    Card(
        Modifier
            .padding(cardSizes.padding)
            .width(cardSizes.cardWidth)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { isDeleteVisible = true },
                    onTap = {
                        if (isDeleteVisible) {
                            isDeleteVisible = false
                        } else {
                            print("Here")
                            if(videoDetails.status=="Downloaded"){
                                navController.navigate(
                                    VideoPlayerScreenDest(
                                        videoDetails.path ?: "",
                                        videoDetails.title ?: "",
                                        videoDetails.videoPath ?: "",
                                        videoDetails.audioPath ?: ""
                                    )
                                )
                            }
                        }
                    }
                )
            }
        ,
//        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(Modifier.width(boxWidth)){
                IconButton(
                    onClick = {
                        onDelete(videoDetails)
                    },
                    modifier = Modifier.align(Alignment.Center),
                    content = {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete",tint = Color.Red)
                    }

                )
            }
            Column(
                Modifier.background(Color.Black)
            ){

                Box(
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if(videoDetails.thumbnailPath.isNullOrBlank()){
                            Image(
                                modifier = Modifier.height(cardSizes.cardWidth / 2),
                                painter = painterResource( R.drawable.thumbnail),
                                contentDescription = "Thumbnail"
                            )
                        }
                        else{
                            // Load image from file path if exists
                            val painter =
                                // You can set various configurations here (placeholders, error handling, etc.)
                                rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(data = videoDetails.thumbnailPath).apply(block = fun ImageRequest.Builder.() {
                                            // You can set various configurations here (placeholders, error handling, etc.)
                                            crossfade(true)
                                        }).build()
                                )
                            Image(
                                modifier = Modifier.fillMaxWidth().height(cardSizes.cardWidth / 2),
                                painter = painter,
                                contentDescription = "Thumbnail",
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardSizes.cardWidth / 2)
                    ) {
                        val resolution = if(videoDetails.width !=null && videoDetails.height!=null) "${videoDetails.width}x${videoDetails.height}" else ""
                        Box(Modifier.clip(RoundedCornerShape(bottomEnd = 4.dp)).background(Color(0x99000000)).padding(start = 4.dp).align(Alignment.TopStart)){
                            Text(videoDetails.extractor?:"", color = Color.White
                                )
                        }
                        Box(Modifier.clip(RoundedCornerShape(bottomStart = 4.dp)).background(Color(0x99000000)).padding(end = 4.dp).align(Alignment.TopEnd)) {
                            Text(
                                resolution, color = Color.White
                            )
                        }

                        if(videoDetails.status=="Downloading Files"){
                            Box(modifier =Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x99000000))
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 4.dp)){
                                Text(videoDetails.speed, fontSize = cardSizes.textSize, color = Color.White)
                            }
                            Box(modifier =Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .align(Alignment.BottomEnd)
                                .background(Color(0x99000000))
                                .padding(horizontal = 4.dp)) {
                                Text(
                                    "${videoDetails.progress}%",
                                    fontSize = cardSizes.textSize
                                    , color = Color.White
                                )
                            }
                            LinearProgressIndicator(
                                progress = { videoDetails.progress.toFloat()/100 },
                                modifier = Modifier.fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .height(5.dp)
                            )                       }
                    }
                    if (videoDetails.thumbnail == null) {
                        Box(
                            modifier = Modifier
                                .background(brush = shimmerBrush())
                                .fillMaxWidth()
                                .height(cardSizes.cardWidth / 2)
                        ) {

                        }
                    }
                }


                Column(Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .wrapContentHeight()) {
                    if (videoDetails.title != null) {
                        Text(videoDetails.title, color = Color.White, fontSize = cardSizes.titleSize)
                    } else {
                        AnimatedBox(width = cardSizes.cardWidth, height = cardSizes.titleSizeDp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {


                        if (videoDetails.duration != null) {
                            Text(videoDetails.duration, color = Color.White, fontSize = cardSizes.textSize)
                        } else {
                            AnimatedBox(width = 30.dp, height = cardSizes.textSizeDp)
                        }

                        if (videoDetails.fileSize != null) {
                            Text(videoDetails.fileSize, color = Color.White, fontSize = cardSizes.textSize)
                        } else {
                            AnimatedBox(width = 30.dp, height = cardSizes.textSizeDp)
                        }
                    }
                }
            }
        }


    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VideoCardPreview() {
    QuickDLTheme {
        val cardSizes = calculateCardSizes()
        val videoDetails = VideoInfo(
            url = "fdfadfsfs",
            title = null,
            path = null,
            videoPath = null,
            audioPath = null,
            thumbnail = null,
            duration = null,
            width = null,
            height = null,
            fileSize = null,
            status = "Downloading Files",
            extractor = null,
            uploader = null,
            audioFormat = null,
            videoFormat = null,
            thumbnailPath = null,
            progress = 100
        )
        val onDelete: (VideoInfo) -> Unit = { videoInfo ->
            // Handle the delete action, for example:
            println("Deleting video: ${videoInfo.title}")
        }
        VideoCard(videoDetails = videoDetails, cardSizes = cardSizes, navController = rememberNavController(), onDelete = onDelete)    }
}