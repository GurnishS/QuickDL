package com.solvynix.quickdl.ui.screens.downloads


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.solvynix.quickdl.models.DownloadsScreenState
import com.solvynix.quickdl.ui.components.SearchBar
import com.solvynix.quickdl.ui.components.VideoGrid


@Composable
fun DownloadsScreen(
    navController: NavController,
    state:DownloadsScreenState,
    onEvent:(DownloadsScreenEvent)->Unit
) {
    print(state.videos)
    Scaffold(
        Modifier.statusBarsPadding()
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            SearchBar(state,onEvent)
            VideoGrid(
                videos = state.videos,
                navController,
                modifier = Modifier
            )
        }


    }
}