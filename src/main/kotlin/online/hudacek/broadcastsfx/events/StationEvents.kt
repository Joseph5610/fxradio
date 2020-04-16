package online.hudacek.broadcastsfx.events

import online.hudacek.broadcastsfx.data.Station
import tornadofx.FXEvent

enum class PlayingStatus {
    Playing, Stopped
}

class StationChangedEvent(val station: Station, val playingStatus: PlayingStatus) : FXEvent()

class StationDirectoryReloadEvent(val country: String) : FXEvent()