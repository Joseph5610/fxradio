package online.hudacek.fxradio.apiclient.radiobrowser.model

import com.google.gson.annotations.SerializedName

data class Tag(val name: String, @SerializedName("stationcount") val stationCount: Int)
