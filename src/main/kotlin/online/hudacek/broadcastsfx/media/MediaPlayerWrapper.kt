package online.hudacek.broadcastsfx.media

import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.broadcastsfx.ConfigValues
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
    private val notification by lazy { find(MainView::class).notification }

    var playerType: PlayerType? = null
        private set(value) {
            with(app.config) {
                set(ConfigValues.keyPlayerType to value)
                save()
            }
        }
        get() {
            return if (field == null) {
                PlayerType.valueOf(app.config.string(
                        ConfigValues.keyPlayerType, "VLC"))
            } else field
        }

    private var mediaPlayer: MediaPlayer = initMediaPlayer(playerType)

    var volume: Double
        get() = mediaPlayer.volume
        set(value) {
            if (mediaPlayer.changeVolume(value)) {
                with(app.config) {
                    set(ConfigValues.keyVolume to value)
                    save()
                }
            }
        }

    init {
        volume = app.config.double(ConfigValues.keyVolume, 0.0)
        subscribe<PlayerTypeChange> { event ->
            with(event) {
                if (playerType != changedPlayerType) {
                    mediaPlayer.releasePlayer()
                    mediaPlayer = initMediaPlayer(changedPlayerType)
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

    private fun initMediaPlayer(playerType: PlayerType?): MediaPlayer {
        return if (playerType == PlayerType.Native) {
            this.playerType = PlayerType.Native
            logger.debug { "said to load native player.. " }
            NativeMediaPlayer()
        } else {
            try {
                this.playerType = PlayerType.VLC
                logger.debug { "trying to init VLC media player " }
                VLCMediaPlayer()
            } catch (e: RuntimeException) {
                e.printStackTrace()
                this.playerType = PlayerType.Native
                logger.debug { "VLC init failed, init native library " }
                NativeMediaPlayer()
            }
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