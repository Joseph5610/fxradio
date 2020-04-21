package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import tornadofx.Controller

class MainController : Controller() {

    fun cancelMediaPlaying() = MediaPlayerWrapper.releasePlayer()
}