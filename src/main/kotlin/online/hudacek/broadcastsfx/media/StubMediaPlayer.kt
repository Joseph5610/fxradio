package online.hudacek.broadcastsfx.media

import mu.KotlinLogging

class StubMediaPlayer : MediaPlayer {
    private val logger = KotlinLogging.logger {}

    init {
        logger.debug { "init stub media player" }
    }

    override fun play(url: String) {
        logger.debug { "stub play()" }
    }

    override fun changeVolume(volume: Double) = false

    override fun cancelPlaying() {
        logger.debug { "stub cancelPlaying()" }
    }

    override fun releasePlayer() {
        logger.debug { "stub releasePlayer()" }
    }
}