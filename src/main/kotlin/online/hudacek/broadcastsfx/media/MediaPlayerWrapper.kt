package online.hudacek.broadcastsfx.media

import mu.KotlinLogging
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.model.PlayerModel
import tornadofx.Component
import tornadofx.ScopedInstance
import tornadofx.onChange
import java.lang.RuntimeException

//TODO get rid of this class in its current form
class MediaPlayerWrapper : Component(), ScopedInstance {

    private val logger = KotlinLogging.logger {}
    private val playerModel: PlayerModel by inject()

    var playingStatus = PlayingStatus.Stopped

    var volume: Double
        get() = app.config.double(Config.volume, -15.0)
        set(value) {
            if (mediaPlayer.changeVolume(value)) {
                with(app.config) {
                    set(Config.volume to value)
                    save()
                }
            }
        }

    private var mediaPlayer: MediaPlayer = StubMediaPlayer()

    init {
        playerModel.playerType.onChange {
            if (it != null) {
                logger.debug { "player type changed: $it" }
                mediaPlayer.releasePlayer()
                mediaPlayer = initMediaPlayer(it)
            }
        }

        subscribe<PlaybackChangeEvent> { event ->
            playingStatus = event.playingStatus
            with(event) {
                if (playingStatus == PlayingStatus.Playing) {
                    play(playerModel.station.value.url_resolved)
                } else {
                    mediaPlayer.cancelPlaying()
                }
            }
        }

        playerModel.station.onChange {
            it?.let {
                play(it.url_resolved)
                playingStatus = PlayingStatus.Playing
            }
        }
    }

    private fun initMediaPlayer(playerType: PlayerType): MediaPlayer {
        logger.debug { "initMediaPlayer $playerType" }
        return if (playerType == PlayerType.Native) {
            logger.debug { "trying to init native player.. " }
            NativeMediaPlayer(this)
        } else {
            try {
                logger.debug { "trying to init VLC media player " }
                VLCMediaPlayer(this)
            } catch (e: RuntimeException) {
                e.printStackTrace()
                logger.debug { "VLC init failed, init native library " }
                NativeMediaPlayer(this)
            }
        }
    }

    private fun play(url: String?) {
        logger.debug { "play() called" }
        url?.let {
            mediaPlayer.cancelPlaying()
            mediaPlayer.play(url)
        }
    }

    fun release() = mediaPlayer.releasePlayer()

    fun handleError(t: Throwable) {
        fire(PlaybackChangeEvent(PlayingStatus.Stopped))
        tornadofx.error("Stream can't be played", t.localizedMessage)
    }
}