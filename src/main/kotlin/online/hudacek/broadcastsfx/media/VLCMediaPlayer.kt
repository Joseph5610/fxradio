package online.hudacek.broadcastsfx.media

import mu.KotlinLogging
import online.hudacek.broadcastsfx.events.PlayingStatus
import uk.co.caprica.vlcj.log.LogEventListener
import uk.co.caprica.vlcj.log.LogLevel
import uk.co.caprica.vlcj.log.NativeLog
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

class VLCMediaPlayer : MediaPlayer {

    private val logger = KotlinLogging.logger {}

    override var playingStatus: PlayingStatus = PlayingStatus.Stopped
    override var volume: Double = 0.0

    private val mediaPlayerComponent by lazy { AudioPlayerComponent() }

    init {
        logger.debug { "VLC player started" }
    }

    override fun play(url: String) {
        mediaPlayerComponent.mediaPlayer().media().play(url)
        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun finished(mediaPlayer: uk.co.caprica.vlcj.player.base.MediaPlayer?) {
                end(0)
            }

            override fun error(mediaPlayer: uk.co.caprica.vlcj.player.base.MediaPlayer?) {
                end(1)
            }

            override fun playing(mediaPlayer: uk.co.caprica.vlcj.player.base.MediaPlayer?) {
                playingStatus = PlayingStatus.Playing
            }
        })
        val log: NativeLog = mediaPlayerComponent.mediaPlayerFactory().application().newLog()
        log.level = LogLevel.NOTICE
        log.addLogListener { level, module, file, line, name, header, id, message ->
            logger.debug { String.format("[%-20s] (%-20s) %7s: %s\n", module, name, level, message) }
        }

    }

    override fun changeVolume(volume: Double): Boolean {
        val intVol = if (volume < -28) {
            0
        } else {
            ((volume + 80) * (100 / 86)).toInt()
        }

        println(intVol)
        return mediaPlayerComponent.mediaPlayer().audio().setVolume(intVol)
    }

    private fun end(result: Int) { // It// s not allowed to call back into LibVLC from an event handling thread, so submit() is used
        logger.debug { "ending current stream if any" }

        if (result == 1) {
            MediaPlayerWrapper.handleError(RuntimeException("Error in playing stream"))
        }

        mediaPlayerComponent.mediaPlayer().submit {
            mediaPlayerComponent.mediaPlayer().controls().stop()
        }
        playingStatus = PlayingStatus.Stopped
    }

    override fun cancelPlaying() = end(0)

    override fun releasePlayer() {
        logger.debug { "releasing player" }

        mediaPlayerComponent.mediaPlayer().submit {
            mediaPlayerComponent.mediaPlayer().controls().stop()
            mediaPlayerComponent.mediaPlayer().release()
        }
    }
}