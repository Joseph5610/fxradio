package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import tornadofx.Controller

class PlayerController : Controller() {

    val mediaPlayer: MediaPlayerWrapper by inject()

    fun togglePlaying() = mediaPlayer.togglePlaying()

    fun getVolume(): Double = mediaPlayer.volume

    fun changeVolume(newVolume: Double) {
        mediaPlayer.volume = newVolume
    }
}