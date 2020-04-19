package online.hudacek.broadcastsfx.media

import javafx.application.Platform
import kotlinx.coroutines.*
import mu.KotlinLogging
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.extension.set
import online.hudacek.broadcastsfx.views.MainView
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.Component

object MediaPlayerWrapper : Component() {

    private val logger = KotlinLogging.logger {}

    private var mediaPlayerCoroutine: Job? = null
    private val mediaPlayer: MediaPlayer = NativeMediaPlayer()

    private val notification by lazy { find(MainView::class).notification }

    val playingStatus: PlayingStatus
        get() {
            return mediaPlayer.playingStatus
        }

    private val handler = CoroutineExceptionHandler { _, exception ->
        mediaPlayer.playingStatus = PlayingStatus.Stopped
        fire(PlaybackChangeEvent(PlayingStatus.Stopped))
        exception.printStackTrace()
        Platform.runLater {
            notification[FontAwesome.Glyph.WARNING] = "Can't open stream: " + exception.localizedMessage
        }
    }

    fun play(url: String) {
        logger.debug { "play() called" }
        cancelPlaying()
        mediaPlayerCoroutine = GlobalScope.launch(handler) {
            mediaPlayer.play(this, url)
        }
    }

    fun changeVolume(volume: Float) = mediaPlayer.changeVolume(volume)

    fun cancelPlaying() {
        mediaPlayer.playingStatus = PlayingStatus.Stopped
        mediaPlayerCoroutine?.isActive?.let {
            logger.debug { "cancelling player" }
            mediaPlayerCoroutine?.cancel()
        }
    }
}