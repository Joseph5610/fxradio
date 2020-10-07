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

import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import javafx.beans.property.Property
import mu.KotlinLogging
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.events.PlaybackChangeEvent
import online.hudacek.fxradio.events.PlayingStatus
import online.hudacek.fxradio.media.player.CustomPlayer
import online.hudacek.fxradio.media.player.VLCPlayer
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

enum class PlayerType {
    Custom, VLC
}

//TODO get rid of this class in its current form
object MediaPlayerWrapper : Component() {

    private val logger = KotlinLogging.logger {}

    private val playerViewModel: PlayerViewModel by inject()

    private var internalMediaPlayer: MediaPlayer = MediaPlayer.stub

    var playingStatus = PlayingStatus.Stopped

    init {
        //Update internal player type
        playerViewModel.playerTypeProperty
                .toObservableChangesNonNull()
                .map { it.newVal }
                .subscribe {
                    logger.info { "Player type has changed: $it" }
                    internalMediaPlayer.releasePlayer()
                    internalMediaPlayer = changePlayer(it)
                }

        //Set volume for current player
        playerViewModel.volumeProperty.onChange {
            logger.debug { "Volume changed: $it" }
            internalMediaPlayer.changeVolume(it)
        }

        //Toggle playing
        subscribe<PlaybackChangeEvent> {
            playingStatus = it.playingStatus
            if (playingStatus == PlayingStatus.Playing) {
                //Ignore stations with empty stream URL
                playerViewModel.stationProperty.value.url_resolved?.let {
                    play(it)
                }
            } else {
                internalMediaPlayer.cancelPlaying()
            }
        }

        playerViewModel.stationProperty.onChange {
            if (it != null && it.isValid()) {
                fire(PlaybackChangeEvent(PlayingStatus.Playing))
            }
        }
    }

    fun init(playerType: Property<PlayerType>) {
        logger.info { "MediaPlayer $playerType initialized" }
        internalMediaPlayer = changePlayer(playerType.value)
    }

    fun release() = internalMediaPlayer.releasePlayer()

    fun togglePlaying() = if (playingStatus == PlayingStatus.Playing) {
        fire(PlaybackChangeEvent(PlayingStatus.Stopped))
    } else {
        fire(PlaybackChangeEvent(PlayingStatus.Playing))
    }

    private fun changePlayer(playerType: PlayerType): MediaPlayer {
        if (playerType == PlayerType.Custom) {
            return CustomPlayer()
        } else {
            return try {
                VLCPlayer()
            } catch (e: Exception) {
                playerViewModel.playerTypeProperty.value = PlayerType.Custom
                logger.error(e) { "VLC player failed to initialize, trying ${PlayerType.Custom} instead" }
                fire(NotificationEvent(messages["player.vlc.error"]))
                CustomPlayer()
            }
        }
    }

    private fun play(url: String) {
        with(internalMediaPlayer) {
            changeVolume(playerViewModel.volumeProperty.value)
            play(url)
        }
    }
}