package online.hudacek.broadcastsfx.events

import tornadofx.FXEvent

enum class PlayingStatus {
    Playing,
    Stopped
}

enum class PlayerType {
    Native, VLC
}

class PlaybackChangeEvent(val playingStatus: PlayingStatus) : FXEvent()

data class MediaMeta(val title: String, val genre: String, val nowPlaying: String)

class MediaMetaChanged(val mediaMeta: MediaMeta) : FXEvent()