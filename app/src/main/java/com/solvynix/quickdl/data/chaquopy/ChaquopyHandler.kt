package com.solvynix.quickdl.data.chaquopy

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.solvynix.quickdl.MainActivity
import org.json.JSONObject
import android.util.Log

class ChaquopyHandler {
    companion object {

        suspend fun getVideoInfo(url: String, videoQuality: Int, audioQuality: String): MutableMap<String, Any?> {
            val videoMap = mutableMapOf<String, Any?>(
                "title" to null, "thumbnail" to null, "duration" to null,
                "width" to null, "height" to null, "fileSize" to null,
                "extractor" to null, "uploader" to null,
                "videoFormat" to null, "audioFormat" to null,
                "status" to "Checking Formats"
            )

            try {
                val py = MainActivity.python
                val module = py.getModule("script")
                val result = module.callAttr("get_video_info", url, videoQuality, audioQuality)

                Log.d("Json String Format", result.toString())
                val jsonObject = JSONObject(result.toString())

                val formatsArray = jsonObject.optJSONArray("requested_format") ?: return videoMap

                val videoFormat = formatsArray.optJSONObject(0) ?: JSONObject()
                val audioFormat = formatsArray.optJSONObject(1) ?: JSONObject()

                val width = videoFormat.optInt("width", 0).takeIf { it > 0 }
                val height = videoFormat.optInt("height", 0).takeIf { it > 0 }
                val fileSize = (videoFormat.optInt("filesize", 0) + audioFormat.optInt("filesize", 0))
                    .takeIf { it > 0 }
                    ?.let { "%.2f MB".format(it / (1024.0 * 1024.0)) } ?: "Unknown"

                val videoFormatString = videoFormat.optString("format", null)
                val audioFormatString = audioFormat.optString("format", null)

                videoMap["title"] = jsonObject.optString("title", "Unknown Title")
                videoMap["thumbnail"] = jsonObject.optString("thumbnail", "Default Thumbnail")
                videoMap["duration"] = jsonObject.optString("duration", "Unknown")
                videoMap["width"] = width
                videoMap["height"] = height
                videoMap["fileSize"] = fileSize
                videoMap["extractor"] = jsonObject.optString("extractor", "Unknown")
                videoMap["uploader"] = jsonObject.optString("uploader", "Unknown")
                videoMap["videoFormat"] = videoFormatString
                videoMap["audioFormat"] = audioFormatString

                Log.d("Video Info", videoMap.toString())

            } catch (e: Exception) {
                Log.e("Fetching Formats", "Error: ${e.message}")
            }
            return videoMap
        }

        suspend fun downloadFormat(url: String, formatId: String, onProgress: (progress: Int, speed: Int) -> Unit):String {
            try {
                val py = MainActivity.python
                val module = py.getModule("script")

                val pyCallback = PyObject.fromJava { jsonStr: String ->
                    try {
                        val jsonObj = JSONObject(jsonStr)
                        val status = jsonObj.optString("status", "")

                        if (status == "downloading") {
                            val speed = jsonObj.optDouble("speed", 0.0).toInt()
                            val progress = jsonObj.optString("_percent_str", "0%")
                                .replace("%", "")
                                .toDoubleOrNull()?.toInt() ?: 0

                            onProgress(progress, speed)
                            Log.d("Download Progress", "Progress: $progress%, Speed: $speed B/s")
                        } else {

                        }
                    } catch (e: Exception) {
                        Log.e("Download Callback", "Error: ${e.message}")
                    }
                }

                val result = module.callAttr("download_format", url, formatId, pyCallback)
                Log.d("Download Result", result.toString())
                return result.toString()
            } catch (e: Exception) {
                Log.e("Downloading Video", "Error: ${e.message}")
            }
        return ""
        }

    }
}
