package online.hudacek.broadcastsfx.data

data class TopStationsModel(
        val changeuuid: String,
        val stationuuid: String,
        val name: String,
        val url: String,
        val url_resolved: String,
        val homepage: String,
        val favicon: String?,
        val tags: String,
        val country: String,
        val countrycode: String,
        val state: String,
        val language: String,
        val votes: Int,
        val lastchangetime: String,
        val codec: String,
        val bitrate: Int,
        val hls: Int,
        val lastcheckok: Int,
        val lastchecktime: String,
        val lastcheckoktime: String,
        val lastlocalchecktime: String,
        val clicktimestamp: String,
        val clickcount: Int,
        val clicktrend: Int
)