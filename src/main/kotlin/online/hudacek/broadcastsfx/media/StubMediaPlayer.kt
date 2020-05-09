package online.hudacek.broadcastsfx.media

import mu.KotlinLogging

class StubMediaPlayer : MediaPlayer {
    private val logger = KotlinLogging.logger {}

    init {
        logger.debug { "init stub media player" }
    }

    override fun play(url: String) {
    }

    override fun changeVolume(volume: Double) = false

    override fun cancelPlaying() {
    }

    override fun releasePlayer() {
    }
}