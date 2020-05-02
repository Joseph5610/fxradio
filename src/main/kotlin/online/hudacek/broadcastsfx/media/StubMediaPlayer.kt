package online.hudacek.broadcastsfx.media

import mu.KotlinLogging
import online.hudacek.broadcastsfx.events.PlayingStatus

class StubMediaPlayer : MediaPlayer {
    private val logger = KotlinLogging.logger {}

    init {
        logger.debug { "init stub media player" }
    }

    override var playingStatus: PlayingStatus = PlayingStatus.Stopped

    override fun play(url: String?) {
    }

    override fun changeVolume(volume: Double): Boolean {
        return false
    }

    override fun cancelPlaying() {
    }

    override fun releasePlayer() {
    }
}