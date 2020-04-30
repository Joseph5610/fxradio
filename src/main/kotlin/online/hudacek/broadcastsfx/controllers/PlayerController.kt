package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import tornadofx.Controller

class PlayerController : Controller() {

    private val mediaPlayer by lazy { MediaPlayerWrapper }

    fun handlePlayerControls() {
        if (mediaPlayer.playingStatus == PlayingStatus.Playing) {
            fire(PlaybackChangeEvent(PlayingStatus.Stopped))
        } else {
            fire(PlaybackChangeEvent(PlayingStatus.Playing))
        }
    }

    fun getVolume() = mediaPlayer.volume

    fun changeVolume(newVolume: Double) {
        mediaPlayer.volume = newVolume
    }
}