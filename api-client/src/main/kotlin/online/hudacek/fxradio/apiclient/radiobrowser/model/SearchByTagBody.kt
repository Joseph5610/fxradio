package online.hudacek.fxradio.apiclient.radiobrowser.model

data class SearchByTagBody(val tag: String, val limit: Int = 200, val hidebroken: Boolean = true)
