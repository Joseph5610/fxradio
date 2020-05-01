package online.hudacek.broadcastsfx.controllers

import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import tornadofx.Controller

class MainController : Controller() {

    private val mediaPlayerWrapper: MediaPlayerWrapper by inject()

    fun cancelMediaPlaying() = mediaPlayerWrapper.release()

}