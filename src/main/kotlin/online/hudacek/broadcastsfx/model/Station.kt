package online.hudacek.broadcastsfx.model

data class Station(
        val changeuuid: String,
        val stationuuid: String,
        val name: String,
        val url: String,
        val url_resolved: String?,
        val homepage: String,
        var favicon: String?,
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
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Station) {
            this.stationuuid == other.stationuuid
        } else super.equals(other)
    }

    override fun hashCode(): Int {
       return super.hashCode()
    }
}