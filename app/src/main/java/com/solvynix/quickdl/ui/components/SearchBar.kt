package com.solvynix.quickdl.ui.components

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solvynix.quickdl.R
import com.solvynix.quickdl.models.DownloadsScreenState
import com.solvynix.quickdl.ui.screens.downloads.DownloadsScreenEvent
//import com.solvynix.quickdl.data.chaquopy.ChaquopyHandler
import com.solvynix.quickdl.ui.theme.QuickDLTheme
import kotlinx.coroutines.*

@Composable
fun SearchBar(state: DownloadsScreenState, onEvent: (DownloadsScreenEvent) -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    fun onDownload() {
        onEvent(DownloadsScreenEvent.onDownloadClicked)
        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
    }

    fun onPaste() {
        clipboardManager.getText()?.let {
            onEvent(DownloadsScreenEvent.setUrl(it.toString()))
            Toast.makeText(context, "Pasted from clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    val isSearchActive = state.inputUrl.isNotEmpty()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            Modifier
                .border(1.dp, Color.Gray, RoundedCornerShape(32.dp))
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = state.inputUrl,
                onValueChange = {onEvent(DownloadsScreenEvent.setUrl(it))},
                singleLine = true,
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search

                ),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (state.inputUrl.isEmpty()) {
                            Row {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Gray
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Search...", color = Color.Gray, fontSize = 18.sp)
                            }

                        }
                        innerTextField()

                    }
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        ElevatedButton(
            onClick = { onDownload() },
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.elevatedButtonColors()
        ) {
            Icon(
                painter = painterResource(R.drawable.download_icon),
                contentDescription = "Download",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


//@Preview(showBackground = true, widthDp = 360)
//@Composable
//fun SearchBarPreview() {
//    QuickDLTheme {
//        SearchBar(state, onEvent)
//    }
//
//}