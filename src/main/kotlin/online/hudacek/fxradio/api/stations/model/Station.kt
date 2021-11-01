package online.hudacek.fxradio.api.stations.model

import online.hudacek.fxradio.FxRadio

/**
 * Station data class
 */
data class Station(
        val stationuuid: String,
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
        var votes: Int = 0,
        val geo_lat: Double = 0.0,
        val geo_long: Double = 0.0,
        val clicktrend: Int = 0,
        val languagecodes: String = "") {

    fun isValid() = stationuuid != "0"

    override fun equals(other: Any?) = if (other is Station) {
        this.stationuuid == other.stationuuid
    } else {
        super.equals(other)
    }

    override fun hashCode() = stationuuid.hashCode()

    companion object {
        val dummy by lazy {
            Station("0", "Not playing", null, FxRadio.appUrl, null)
        }
    }
}

//Contains tag or country name of station
internal val Station.tagsSplit: String
    get() {
        val stationTagsSplit = tags.split(",")
        return when {
            tags.isEmpty() -> country
            stationTagsSplit.size > 1 -> stationTagsSplit[0].capitalize() + ", " + stationTagsSplit[1].capitalize()
            else -> stationTagsSplit[0].capitalize()
        }
    }

