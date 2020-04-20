package online.hudacek.broadcastsfx.events

import tornadofx.FXEvent

enum class StationDirectoryType {
    Favourites, Country, TopList
}

class StationListReloadEvent(val country: String, val type: StationDirectoryType) : FXEvent()