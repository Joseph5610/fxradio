package online.hudacek.broadcastsfx.events

import tornadofx.FXEvent

enum class StationListType {
    Favourites, Country, TopStations
}

class StationListReloadEvent(val type: StationListType, val params: String? = null) : FXEvent()