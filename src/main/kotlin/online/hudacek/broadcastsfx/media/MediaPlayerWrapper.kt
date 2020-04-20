package online.hudacek.broadcastsfx.media

import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayerTypeChange
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.extension.set
import online.hudacek.broadcastsfx.views.MainView
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.Component
import java.lang.RuntimeException
import kotlin.properties.Delegates

object MediaPlayerWrapper : Component() {

    private val logger = KotlinLogging.logger {}

    private val mediaPlayer: MediaPlayer = initMediaPlayer()

    private val notification by lazy { find(MainView::class).notification }

    init {
        mediaPlayer.volume = config.double("volume", 0.0)
    }

    var isNativePlayer: Boolean by Delegates.observable(false) { _, _, newValue ->
        if (newValue) fire(PlayerTypeChange(PlayerType.Native))
        else fire(PlayerTypeChange(PlayerType.VLC))
    }

    val playingStatus: PlayingStatus
        get() {
            return mediaPlayer.playingStatus
        }

    private fun initMediaPlayer(): MediaPlayer {
        return try {
            logger.debug { "trying to init VLC media player " }
            VLCMediaPlayer()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            isNativePlayer = true
            logger.debug { "VLC init failed, init native library " }
            NativeMediaPlayer()
        }
    }

    fun handleError(e: Throwable) {
        mediaPlayer.playingStatus = PlayingStatus.Stopped
        fire(PlaybackChangeEvent(PlayingStatus.Stopped))
        e.printStackTrace()
        Platform.runLater {
            notification[FontAwesome.Glyph.WARNING] = "Can't open stream: " + e.localizedMessage
        }
    }

    fun play(url: String) {
        logger.debug { "play() called" }
        cancelPlaying()
        mediaPlayer.play(url)
    }

    fun changeVolume(volume: Double) = mediaPlayer.changeVolume(volume)

    fun cancelPlaying() = mediaPlayer.cancelPlaying()

    fun releasePlayer() = mediaPlayer.releasePlayer()
}