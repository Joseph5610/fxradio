package online.hudacek.broadcastsfx.events

import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.FXEvent

enum class PlayingStatus {
    Playing,
    Stopped
}

enum class PlayerType {
    Native, VLC
}

class PlaybackChangeEvent(val playingStatus: PlayingStatus) : FXEvent() {
    constructor(station: Station?, playingStatus: PlayingStatus) : this(playingStatus)
}

class PlayerTypeChange(val changedPlayerType: PlayerType) : FXEvent()
