package online.hudacek.broadcastsfx.events

import online.hudacek.broadcastsfx.data.Station
import tornadofx.FXEvent

enum class PlayingStatus {
    Playing, Stopped
}

enum class StationDirectoryType {
    Favourites, Country, TopList
}

class StationChangedEvent(val station: Station, val playingStatus: PlayingStatus) : FXEvent()

class StationListReloadEvent(val country: String, val type: StationDirectoryType) : FXEvent()