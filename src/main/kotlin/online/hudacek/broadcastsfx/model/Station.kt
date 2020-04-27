package online.hudacek.broadcastsfx.model

import tornadofx.*

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

    fun isInvalidImage() = favicon.isNullOrEmpty() || favicon!!.contains(".ico")

    override fun equals(other: Any?): Boolean {
        return if (other is Station) {
            this.stationuuid == other.stationuuid
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

class CurrentStation(station: Station) {
    var station: Station by property(station)
    fun stationProperty() = getProperty(CurrentStation::station)
}


class StationViewModel : ItemViewModel<CurrentStation>() {
    private val stationHistory: StationHistoryViewModel by inject()

    val station = bind { item?.stationProperty() }

    init {
        station.onChange {
            if (it != null) {
                stationHistory.add(it)
            }
        }
    }
}

class StationHistory {
    val stations = observableListOf<Station>()
}

class StationHistoryViewModel : ItemViewModel<StationHistory>() {
    val stations = bind(StationHistory::stations)

    fun add(station: Station) {
        with(stations.value) {
            if (size > 10) {
                removeAt(0)
            }
            add(station)
        }
    }
}