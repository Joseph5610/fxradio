package online.hudacek.fxradio.apiclient.radiobrowser.model

data class SearchByTagRequest(val tag: String, val limit: Int = 200, val hidebroken: Boolean = true)
