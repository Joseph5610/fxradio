package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import online.hudacek.broadcastsfx.media.NativeMediaPlayer
import tornadofx.Controller

class MainController : Controller() {

    private val mediaPlayer = MediaPlayerWrapper

    fun cancelMediaPlaying() = mediaPlayer.cancelPlaying()
}