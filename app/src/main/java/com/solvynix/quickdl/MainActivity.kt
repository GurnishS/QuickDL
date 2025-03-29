package com.solvynix.quickdl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.solvynix.quickdl.data.local.VideoDatabase
import com.solvynix.quickdl.ui.screens.downloads.DownloadsScreen
import com.solvynix.quickdl.ui.screens.downloads.DownloadsViewModel
import com.solvynix.quickdl.ui.screens.player.VideoPlayerScreen
import com.solvynix.quickdl.ui.theme.QuickDLTheme


class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            VideoDatabase::class.java,
            "videos.db"
        ).build()
    }

    private val viewModel by viewModels<DownloadsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DownloadsViewModel(db.videoDao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
            println("✅ Python Initialized!")
        } else {
            println("⚡ Python Already Running!")
        }

        setContent {
            QuickDLTheme {
                val navController = rememberNavController()
                val state by viewModel.state.collectAsState()
                NavHost(
                    navController = navController,
                    startDestination = DownloadsScreenDest
                ) {
                    composable<DownloadsScreenDest> {
                        DownloadsScreen(
                            navController,
                            state,
                            viewModel::onEvent
                        )
                    }
                    composable<VideoPlayerScreenDest> {
                        val args = it.toRoute<VideoPlayerScreenDest>()
                        VideoPlayerScreen(
                            navController,
                            args.uri,
                            args.title,
                            args.videoUri,
                            args.audioUri
                        )
                    }
                }
            }
        }
    }


    companion object {
        val python: Python by lazy {
            Python.getInstance()
        }
    }

}
