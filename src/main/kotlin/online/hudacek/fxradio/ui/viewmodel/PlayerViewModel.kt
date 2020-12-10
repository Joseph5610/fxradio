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

package online.hudacek.fxradio.ui.viewmodel

import io.reactivex.disposables.Disposable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import mu.KotlinLogging
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.media.MediaPlayerWrapper
import online.hudacek.fxradio.media.PlayerType
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.applySchedulers
import online.hudacek.fxradio.utils.saveProperties
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.ItemViewModel
import tornadofx.get
import tornadofx.onChange
import tornadofx.property

enum class PlayingStatus {
    Playing, Stopped, Error
}

class PlayerModel(station: Station = Station.stub,
                  animate: Boolean = true,
                  playerType: PlayerType, notifications: Boolean = true,
                  volume: Double,
                  playingStatus: PlayingStatus = PlayingStatus.Stopped, trackName: String = "") {

    val animate: Boolean by property(animate)
    val notifications: Boolean by property(notifications)
    val station: Station by property(station)
    val playerType: PlayerType by property(playerType)
    val volume: Double by property(volume)
    val playingStatus: PlayingStatus by property(playingStatus)
    val trackName: String by property(trackName)
}

/**
 * Player view model
 * -------------------
 * Stores player settings, toggles playing
 * Increment station history list
 * Used all around the app
 */
class PlayerViewModel : ItemViewModel<PlayerModel>() {

    private val logger = KotlinLogging.logger {}

    private val historyViewModel: HistoryViewModel by inject()

    val animateProperty = bind(PlayerModel::animate) as BooleanProperty
    val notificationsProperty = bind(PlayerModel::notifications) as BooleanProperty
    val stationProperty = bind(PlayerModel::station) as ObjectProperty
    val playerTypeProperty = bind(PlayerModel::playerType) as ObjectProperty
    val volumeProperty = bind(PlayerModel::volume) as DoubleProperty
    val playingStatusProperty = bind(PlayerModel::playingStatus) as ObjectProperty
    val trackNameProperty = bind(PlayerModel::trackName) as StringProperty

    init {
        stationProperty.onChange {
            it?.let {
                if (it.isValid()) {
                    historyViewModel.add(it)

                    //Restart playing status
                    playingStatusProperty.value = PlayingStatus.Stopped
                    playingStatusProperty.value = PlayingStatus.Playing

                    //Update the name of the station
                    trackNameProperty.value = it.name + " - " + messages["player.noMetaData"]

                    //Increase count of the station
                    StationsApi.service
                            .click(it.stationuuid)
                            .compose(applySchedulers())
                            .subscribe({
                                logger.debug { "Click registered: $it" }
                            }, {
                                logger.debug { "Click registering failed, ignoring the response" }
                            })
                }
            }
        }

        playerTypeProperty.onChange {
            it?.let {
                playingStatusProperty.value = PlayingStatus.Stopped
                MediaPlayerWrapper.init(it)

                if (it == PlayerType.Custom) {
                    fire(NotificationEvent(messages["player.ffmpeg.info"]))
                }
            }
        }

        //Set volume for current player
        //Save the ViewModel after setting new value
        volumeProperty.onChange {
            MediaPlayerWrapper.changeVolume(it)
        }

        playingStatusProperty.onChange {
            if (it == PlayingStatus.Playing) {
                //Ignore stations with empty stream URL
                stationProperty.value.url_resolved?.let { url ->
                    with(MediaPlayerWrapper) {
                        if (isInitialized) {
                            changeVolume(volumeProperty.value)
                            play(url)
                        } else {
                            //Error while initializing player
                            fire(NotificationEvent(messages["player.init.error"]))
                        }
                    }
                }
            } else {
                MediaPlayerWrapper.stop()
            }
        }
    }

    fun releasePlayer() = MediaPlayerWrapper.release()

    fun togglePlayer() {
        if (playingStatusProperty.value == PlayingStatus.Playing) {
            playingStatusProperty.value = PlayingStatus.Stopped
        } else {
            playingStatusProperty.value = PlayingStatus.Playing
        }
    }

    //Increase vote count on the server
    fun addVote(): Disposable = StationsApi.service
            .vote(stationProperty.value.stationuuid)
            .compose(applySchedulers())
            .subscribe({
                if (!it.ok) {
                    //Why this API returns error 200 on error is beyond me..
                    fire(NotificationEvent(messages["vote.error"]))
                } else {
                    fire(NotificationEvent(messages["vote.ok"], FontAwesome.Glyph.CHECK))
                }
            }, {
                fire(NotificationEvent(messages["vote.error"]))
            })

    override fun onCommit() {
        //Save API server
        saveProperties(
                listOf(
                        Pair(Properties.PLAYER_ANIMATE, animateProperty.value),
                        Pair(Properties.PLAYER, playerTypeProperty.value),
                        Pair(Properties.NOTIFICATIONS, notificationsProperty.value),
                        Pair(Properties.VOLUME, volumeProperty.value)
                )
        )
    }
}

