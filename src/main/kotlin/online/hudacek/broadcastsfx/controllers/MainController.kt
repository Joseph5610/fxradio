package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import tornadofx.Controller

class MainController : Controller() {

    private val mediaPlayer = MediaPlayerWrapper

    fun cancelMediaPlaying() = mediaPlayer.releasePlayer()
}