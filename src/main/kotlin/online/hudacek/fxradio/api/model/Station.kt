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

    companion object {
        val stub by lazy {
            Station("0", "Not playing", null,
                    FxRadio.appUrl, null)
        }
    }
}

