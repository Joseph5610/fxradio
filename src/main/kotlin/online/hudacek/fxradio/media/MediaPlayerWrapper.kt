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

package online.hudacek.fxradio.media

import com.github.thomasnield.rxkotlinfx.toObservableChanges
import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.events.PlaybackChangeEvent
import online.hudacek.fxradio.events.PlayingStatus
import online.hudacek.fxradio.media.player.CustomPlayer
import online.hudacek.fxradio.media.player.VLCPlayer
import online.hudacek.fxradio.viewmodel.PlayerModel
import tornadofx.*

private val logger = KotlinLogging.logger {}

enum class PlayerType {
    Custom, VLC
}

//TODO get rid of this class in its current form
class MediaPlayerWrapper : Component(), ScopedInstance {

    private val playerModel: PlayerModel by inject()

    private var internalMediaPlayer: MediaPlayer = MediaPlayer.stub
    private var internalPlayingStatus = PlayingStatus.Stopped
    private var internalVolume = 0.0

    init {
        //Update internal player type
        playerModel.playerType.toObservableChanges()
                .map { it.newVal }
                .subscribe {
                    logger.info { "player type changed: $it" }
                    internalMediaPlayer.releasePlayer()
                    internalMediaPlayer = changePlayer(it)
                }

        //Set volume for current player
        playerModel.volumeProperty.toObservableChangesNonNull()
                .map { it.newVal.toDouble() }
                .subscribe {
                    logger.debug { "volume changed: $it" }
                    internalVolume = it
                    internalMediaPlayer.changeVolume(it)
                }

        //Toggle playing
        subscribe<PlaybackChangeEvent> {
            internalPlayingStatus = it.playingStatus
            if (it.playingStatus == PlayingStatus.Playing) {
                play(playerModel.stationProperty.value.url_resolved)
            } else {
                internalMediaPlayer.cancelPlaying()
            }
        }

        playerModel.stationProperty.toObservableChangesNonNull()
                .filter { it.newVal.isValidStation() }
                .map { it.newVal }
                .subscribe {
                    fire(PlaybackChangeEvent(PlayingStatus.Playing))
                }
    }

    fun init() {
        logger.info { "init MediaPlayerWrapper with MediaPlayer $internalMediaPlayer" }
    }

    private fun changePlayer(playerType: PlayerType): MediaPlayer {
        if (playerType == PlayerType.Custom) {
            return CustomPlayer()
        } else {
            return try {
                VLCPlayer()
            } catch (e: Exception) {
                playerModel.playerType.value = PlayerType.Custom
                logger.error(e) { "VLC init failed, init native library" }
                fire(NotificationEvent(messages["player.vlc.error"]))
                CustomPlayer()
            }
        }
    }

    private fun play(url: String?) {
        if (url != null) {
            internalMediaPlayer.apply {
                changeVolume(internalVolume)
                play(url)
            }
        }
    }

    fun release() = internalMediaPlayer.releasePlayer()

    fun togglePlaying() {
        if (internalPlayingStatus == PlayingStatus.Playing) {
            fire(PlaybackChangeEvent(PlayingStatus.Stopped))
        } else {
            fire(PlaybackChangeEvent(PlayingStatus.Playing))
        }
    }

    companion object : Component() {
        fun handleError(t: Throwable) {
            Platform.runLater {
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
                fire(NotificationEvent(t.localizedMessage))
                logger.error(t) { "Stream can't be played" }
            }
        }
    }
}