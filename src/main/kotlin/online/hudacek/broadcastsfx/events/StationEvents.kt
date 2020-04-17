package online.hudacek.broadcastsfx.events

import tornadofx.FXEvent

enum class PlayingStatus {
    Playing, Stopped
}

enum class StationDirectoryType {
    Favourites, Country, TopList
}

class PlaybackChangeEvent(val playingStatus: PlayingStatus) : FXEvent()

class StationListReloadEvent(val country: String, val type: StationDirectoryType) : FXEvent()