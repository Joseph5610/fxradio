package online.hudacek.broadcastsfx.events

import tornadofx.FXEvent

enum class PlayingStatus {
    Playing, Stopped
}

enum class PlayerType {
    Native, VLC
}

class PlaybackChangeEvent(val playingStatus: PlayingStatus) : FXEvent()

class PlayerTypeChange(val playerType: PlayerType) : FXEvent()
