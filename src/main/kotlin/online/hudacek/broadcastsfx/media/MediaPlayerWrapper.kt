package online.hudacek.broadcastsfx.media

import mu.KotlinLogging
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayerTypeChange
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.model.CurrentStationModel
import tornadofx.Component
import tornadofx.ScopedInstance
import tornadofx.onChange
import java.lang.RuntimeException

class MediaPlayerWrapper : Component(), ScopedInstance {

    private val logger = KotlinLogging.logger {}
    private val currentStation: CurrentStationModel by inject()

    val playingStatus: PlayingStatus
        get() {
            return mediaPlayer.playingStatus
        }

    var playerType: PlayerType? = null
        private set(value) {
            with(app.config) {
                set(Config.playerType to value)
                save()
            }
        }
        get() {
            return if (field == null) {
                PlayerType.valueOf(app.config.string(
                        Config.playerType, "VLC"))
            } else field
        }

    private var mediaPlayer: MediaPlayer = initMediaPlayer(playerType)

    var volume: Double
        get() = mediaPlayer.volume
        set(value) {
            if (mediaPlayer.changeVolume(value)) {
                with(app.config) {
                    set(Config.volume to value)
                    save()
                }
            }
        }

    init {
        volume = app.config.double(Config.volume, 0.0)
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
                if (playingStatus == PlayingStatus.Playing) {
                    play(currentStation.station.value.url_resolved)
                } else {
                    mediaPlayer.cancelPlaying()
                }
            }
        }

        currentStation.station.onChange {
            it?.let {
                play(it.url_resolved)
            }
        }
    }

    private fun initMediaPlayer(playerType: PlayerType?): MediaPlayer {
        return if (playerType == PlayerType.Native) {
            this.playerType = PlayerType.Native
            logger.debug { "said to load native player.. " }
            NativeMediaPlayer(this)
        } else {
            try {
                this.playerType = PlayerType.VLC
                logger.debug { "trying to init VLC media player " }
                VLCMediaPlayer(this)
            } catch (e: RuntimeException) {
                e.printStackTrace()
                this.playerType = PlayerType.Native
                logger.debug { "VLC init failed, init native library " }
                NativeMediaPlayer(this)
            }
        }
    }

    private fun play(url: String?) {
        logger.debug { "play() called" }
        url.let {
            mediaPlayer.cancelPlaying()
            mediaPlayer.play(url)
        }
    }

    fun release() = mediaPlayer.releasePlayer()

    fun handleError(t: Throwable) {
        mediaPlayer.playingStatus = PlayingStatus.Stopped
        fire(PlaybackChangeEvent(PlayingStatus.Stopped))
        tornadofx.error("Stream can't be played", t.localizedMessage)
    }
}