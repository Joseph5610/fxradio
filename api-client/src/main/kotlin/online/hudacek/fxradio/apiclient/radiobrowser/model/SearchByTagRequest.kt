package online.hudacek.fxradio.apiclient.radiobrowser.model

import com.google.gson.annotations.SerializedName

data class SearchByTagRequest(
    val tag: String,
    val limit: Int = 200,
    @SerializedName("hidebroken") val hideBroken: Boolean = true
)
