package online.hudacek.fxradio.api.model

import online.hudacek.fxradio.FxRadio

/**
 * Stations json structure
 */
data class Station(val stationuuid: String,
                   val name: String,
                   val url_resolved: String?,
                   val homepage: String,
                   var favicon: String?,
                   val tags: String = "",
                   val country: String = "",
                   val countrycode: String = "",
                   val state: String = "",
                   val language: String = "",
                   val codec: String = "",
                   val bitrate: Int = 0,
                   var votes: Int = 0) {

    fun isValid() = stationuuid != "0"

    override fun equals(other: Any?): Boolean {
        return if (other is Station) {
            this.stationuuid == other.stationuuid
        } else super.equals(other)
    }

    override fun hashCode() = super.hashCode()

    override fun toString(): String {
        return "Station(stationuuid='$stationuuid'\n, " +
                "name='$name'\n," +
                "url_resolved=$url_resolved\n, " +
                "homepage='$homepage'\n, " +
                "favicon=$favicon\n, " +
                "tags='$tags'\n, " +
                "country='$country'\n, " +
                "countrycode='$countrycode'\n, " +
                "state='$state'\n, " +
                "language='$language'\n, " +
                "codec='$codec'\n, " +
                "bitrate=$bitrate,\n" +
                " votes=$votes)"
    }

    companion object {
        val stub by lazy {
            Station("0", "Not playing", null,
                    FxRadio.appUrl, null)
        }
    }
}

