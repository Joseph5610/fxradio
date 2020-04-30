package online.hudacek.broadcastsfx.events

import tornadofx.FXEvent

enum class PlayingStatus {
    Playing,
    Stopped
}

enum class PlayerType {
    Native, VLC
}

class PlaybackChangeEvent(val playingStatus: PlayingStatus, params: Any? = null) : FXEvent()

class PlayerTypeChange(val changedPlayerType: PlayerType) : FXEvent()
