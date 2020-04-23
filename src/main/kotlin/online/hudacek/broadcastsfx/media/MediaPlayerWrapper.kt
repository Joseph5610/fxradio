package online.hudacek.broadcastsfx.media

import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayerTypeChange
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.ui.set
import online.hudacek.broadcastsfx.views.MainView
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.Component
import java.lang.RuntimeException

object MediaPlayerWrapper : Component() {

    private val logger = KotlinLogging.logger {}

    private var mediaPlayer: MediaPlayer = initMediaPlayer()
    private val notification by lazy { find(MainView::class).notification }

    var playerType: PlayerType = PlayerType.VLC

    var volume: Double
        get() = mediaPlayer.volume
        set(value) {
            if (mediaPlayer.changeVolume(value)) {
                with(config) {
                    set("volume" to value)
                    save()
                }
            }
        }

    init {
        volume = config.double("volume", 0.0)
        subscribe<PlayerTypeChange> { event ->
            with(event) {
                if (playerType != changedPlayerType) {
                    mediaPlayer.releasePlayer()
                    mediaPlayer = if (changedPlayerType == PlayerType.VLC) VLCMediaPlayer()
                    else NativeMediaPlayer()
                    playerType = changedPlayerType
                }
            }
        }

        subscribe<PlaybackChangeEvent> { event ->
            with(event) {
                if (playingStatus == PlayingStatus.Stopped) {
                    mediaPlayer.cancelPlaying()
                }
            }
        }
    }

    private fun initMediaPlayer(): MediaPlayer {
        return try {
            logger.debug { "trying to init VLC media player " }
            VLCMediaPlayer()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            playerType = PlayerType.Native
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
        mediaPlayer.cancelPlaying()
        mediaPlayer.play(url)
    }

    fun releasePlayer() = mediaPlayer.releasePlayer()
}