package com.cornerd.vidforgeai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

data class VideoJob(
    val prompt: String,
    val duration: String,
    val ratio: String,
    val url: String?,
    val status: String = "queued"
)

class VideoViewModel : ViewModel() {

    var loading by androidx.compose.runtime.mutableStateOf(false)
        private set

    var error by androidx.compose.runtime.mutableStateOf<String?>(null)
        private set

    var lastUrl by androidx.compose.runtime.mutableStateOf<String?>(null)
        private set

    var history by androidx.compose.runtime.mutableStateOf(listOf<VideoJob>())
        private set

    private val client = OkHttpClient()

    fun generate(
        prompt: String,
        duration: String,
        ratio: String
    ) {
        loading = true
        error = null
        lastUrl = null

        viewModelScope.launch {
            try {

                // Create the video generation request
                val create = withContext(Dispatchers.IO) {

                    val json = JSONObject()
                        .put("prompt", prompt)
                        .put(
                            "duration",
                            duration.removeSuffix("s").toInt()
                        )
                        .put("aspect_ratio", ratio)
                        .put("resolution", "720p")

                    val body = json.toString()
                        .toRequestBody(
                            "application/json".toMediaType()
                        )

                    val req = Request.Builder()
                        .url(VideoApi.BASE_URL + "/generate")
                        .post(body)
                        .build()

                    client.newCall(req).execute().use { response ->

                        val text =
                            response.body?.string().orEmpty()

                        if (!response.isSuccessful) {
                            throw Exception(
                                "Server error ${response.code}: $text"
                            )
                        }

                        JSONObject(text)
                    }
                }

                val id = create.getString("id")

                history = listOf(
                    VideoJob(
                        prompt = prompt,
                        duration = duration,
                        ratio = ratio,
                        url = null,
                        status = "starting"
                    )
                ) + history

                // Poll the backend until the video is finished.
                repeat(60) {

                    delay(3000)

                    val result = withContext(Dispatchers.IO) {

                        val req = Request.Builder()
                            .url(
                                VideoApi.BASE_URL +
                                    "/generate/$id"
                            )
                            .get()
                            .build()

                        client.newCall(req)
                            .execute()
                            .use { response ->

                                val text =
                                    response.body?.string()
                                        .orEmpty()

                                if (!response.isSuccessful) {
                                    throw Exception(
                                        "Status error ${response.code}: $text"
                                    )
                                }

                                JSONObject(text)
                            }
                    }

                    val status =
                        result.optString("status")

                    if (status == "succeeded") {

                        val url =
                            result.optString("url")

                        lastUrl = url

                        history =
                            history.mapIndexed { index, job ->

                                if (index == 0) {
                                    job.copy(
                                        url = url,
                                        status = status
                                    )
                                } else {
                                    job
                                }
                            }

                        loading = false
                        return@launch
                    }

                    if (
                        status == "failed" ||
                        status == "canceled"
                    ) {

                        error = result.optString(
                            "error",
                            "Video generation failed."
                        )

                        loading = false
                        return@launch
                    }
                }

                error =
                    "Generation is taking longer than expected. Check Projects shortly."

                loading = false

            } catch (e: Exception) {

                error =
                    e.message
                        ?: "Could not connect to the video server."

                loading = false
            }
        }
    }
}

object VideoApi {

    // CORNER-D AI production backend
    const val BASE_URL =
        "https://corner-d-ai.onrender.com"
}
