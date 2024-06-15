package online.hudacek.fxradio.apiclient.radiobrowser.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Tag(val name: String, @JsonProperty("stationcount") val stationCount: Int)
