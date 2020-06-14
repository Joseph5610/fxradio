/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.broadcastsfx.media

import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.broadcastsfx.events.*
import online.hudacek.broadcastsfx.model.PlayerModel
import tornadofx.*

//TODO get rid of this class in its current form
class MediaPlayerWrapper : Component(), ScopedInstance {

    private val logger = KotlinLogging.logger {}
    private val playerModel: PlayerModel by inject()

    var playingStatus = PlayingStatus.Stopped

    private var mediaPlayer: MediaPlayer = StubMediaPlayer()

    private var internalVolume = 0.0

    init {
        playerModel.playerType.onChange {
            if (it != null) {
                logger.info { "player type changed: $it" }
                mediaPlayer.releasePlayer()
                mediaPlayer = initMediaPlayer(it)
            }
        }

        playerModel.volumeProperty.onChange {
            logger.info { "volume changed: $it" }
            internalVolume = it
            mediaPlayer.changeVolume(it)
        }

        subscribe<PlaybackChangeEvent> {
            playingStatus = it.playingStatus
            if (it.playingStatus == PlayingStatus.Playing) {
                play(playerModel.station.value.url_resolved)
            } else {
                mediaPlayer.cancelPlaying()
            }
        }

        playerModel.station.onChange {
            it?.let {
                if (it.isValidStation()) {
                    play(it.url_resolved)
                    playingStatus = PlayingStatus.Playing
                }
            }
        }
    }

    fun init() {

    }

    private fun initMediaPlayer(playerType: PlayerType): MediaPlayer {
        return if (playerType == PlayerType.Native) {
            logger.debug { "trying to init native player " }
            NativeMediaPlayer(this)
        } else {
            try {
                logger.debug { "trying to init VLC media player " }
                VLCMediaPlayer(this)
            } catch (e: Exception) {
                playerModel.playerType.value = PlayerType.Native
                logger.error(e) {
                    "VLC init failed, init native library "
                }
                fire(NotificationEvent("Player can't be initialized. Library is not installed on the system."))
                NativeMediaPlayer(this)
            }
        }
    }

    private fun play(url: String?) {
        url?.let {
            mediaPlayer.apply {
                changeVolume(internalVolume)
                cancelPlaying()
                play(url)
            }
        }
    }

    fun release() = mediaPlayer.releasePlayer()

    fun handleError(t: Throwable) {
        Platform.runLater {
            fire(PlaybackChangeEvent(PlayingStatus.Stopped))
            fire(NotificationEvent("Stream can't be played: ${t.localizedMessage}"))
            logger.error(t) {
                "Stream can't be played"
            }
        }
    }

    fun mediaMetaChanged(mediaMeta: MediaMeta) = fire(MediaMetaChanged(mediaMeta))

    fun togglePlaying() {
        if (playingStatus == PlayingStatus.Playing) {
            fire(PlaybackChangeEvent(PlayingStatus.Stopped))
        } else {
            fire(PlaybackChangeEvent(PlayingStatus.Playing))
        }
    }
}