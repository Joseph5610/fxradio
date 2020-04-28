package online.hudacek.broadcastsfx.media

import online.hudacek.broadcastsfx.events.PlayingStatus

internal interface MediaPlayer {

    var playingStatus: PlayingStatus
    var volume: Double

    fun play(url: String)
    fun changeVolume(volume: Double): Boolean
    fun cancelPlaying()
    fun releasePlayer()
}