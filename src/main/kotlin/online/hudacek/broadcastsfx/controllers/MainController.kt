package online.hudacek.broadcastsfx.controllers

import mu.KotlinLogging
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import tornadofx.Controller

class MainController : Controller() {

    private val logger = KotlinLogging.logger {}

    fun cancelMediaPlaying() {
        logger.debug { "cancelling media player" }
        MediaPlayerWrapper.releasePlayer()
    }
}