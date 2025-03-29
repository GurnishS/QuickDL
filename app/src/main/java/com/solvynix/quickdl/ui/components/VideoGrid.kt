package com.solvynix.quickdl.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solvynix.quickdl.data.local.VideoInfo
import com.solvynix.quickdl.models.CardSizes

@Composable
fun ConvertSpToDp(spValue: Int): Dp {
    val density = LocalDensity.current
    return with(density) { spValue.sp.toDp() }
}

@Composable
fun calculateCardSizes(): CardSizes {
    val scWidth= LocalConfiguration.current.screenWidthDp
    var cardWidth=400.dp
    var padding=16.dp
    var textSize=10.sp
    var titleSize=14.sp
    if(scWidth<=560){
        padding=8.dp
        cardWidth=(scWidth-16).dp

    }
    else if(scWidth in 561..880){
        textSize=12.sp
        titleSize=16.sp
        padding=8.dp
        cardWidth=((scWidth-24)/2).dp
    }
    else if(scWidth in 881..1200){
        textSize=12.sp
        titleSize=16.sp
        padding=16.dp
        cardWidth=((scWidth-64)/3).dp
    }
    else if(scWidth in 1200.. 1600){
        textSize=16.sp
        titleSize=20.sp
        padding=16.dp
        cardWidth=((scWidth-80)/4).dp
    }
    else{
        textSize=16.sp
        titleSize=20.sp
        padding=32.dp
        cardWidth=(scWidth*0.2f).dp
    }
    val cardSizes= CardSizes(
        cardWidth=cardWidth,
        padding=padding,
        textSize=textSize,
        titleSize=titleSize,
        titleSizeDp=ConvertSpToDp(titleSize.value.toInt()),
        textSizeDp=ConvertSpToDp(textSize.value.toInt())
    )
    return cardSizes

}

@Composable
fun VideoGrid(
    videos: List<VideoInfo>,
    navController: NavController,
    modifier: Modifier,
){
    val cardSizes= calculateCardSizes()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = cardSizes.cardWidth),
        modifier = Modifier.fillMaxWidth().padding(horizontal = cardSizes.padding),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(cardSizes.padding)
    ) {
        for (video in videos) {
            item {
                VideoCard(video,cardSizes,navController)
            }
        }

    }

}

//@Preview
//@Composable
//fun DownloadGridPreview(){
//    QuickDLTheme {
//        VideoGrid()

//    }
//}