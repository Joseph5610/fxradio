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

import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import io.reactivex.Observable
import io.reactivex.Single
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import mu.KotlinLogging
import online.hudacek.fxradio.NotificationPaneEvent
import online.hudacek.fxradio.Properties
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.api.model.ClickResponse
import online.hudacek.fxradio.api.model.Station
import online.hudacek.fxradio.media.MediaPlayerWrapper
import online.hudacek.fxradio.media.PlayerType
import online.hudacek.fxradio.saveProperties
import online.hudacek.fxradio.utils.applySchedulers
import tornadofx.ItemViewModel
import tornadofx.get
import tornadofx.onChange
import tornadofx.property

private val logger = KotlinLogging.logger {}

enum class PlayerState {
    Playing, Stopped, Error
}

class PlayerModel(station: Station = Station.stub,
                  animate: Boolean = true,
                  playerType: PlayerType,
                  volume: Double,
                  playerState: PlayerState = PlayerState.Stopped,
                  trackName: String = "") {

    var animate: Boolean by property(animate)
    var station: Station by property(station)
    var playerType: PlayerType by property(playerType)
    var volume: Double by property(volume)
    var playerState: PlayerState by property(playerState)
    var trackName: String by property(trackName)
}

/**
 * Player view model
 * -------------------
 * Stores player settings, toggles playing
 * Increment station history list
 * Used all around the app
 */
class PlayerViewModel : ItemViewModel<PlayerModel>() {

    val stationProperty = bind(PlayerModel::station) as ObjectProperty
    val playerStateProperty = bind(PlayerModel::playerState) as ObjectProperty

    val playerTypeProperty = bind(PlayerModel::playerType) as ObjectProperty
    val animateProperty = bind(PlayerModel::animate) as BooleanProperty
    val volumeProperty = bind(PlayerModel::volume) as DoubleProperty
    val trackNameProperty = bind(PlayerModel::trackName) as StringProperty

    val stationChanges: Observable<Station> = stationProperty
            .toObservableChangesNonNull()
            .map { it.newVal }

    init {
        stationChanges
                .filter { it.isValid() }
                .flatMapSingle {
                    //Restart playing status
                    playerStateProperty.value = PlayerState.Stopped
                    playerStateProperty.value = PlayerState.Playing

                    //Update the name of the station
                    trackNameProperty.value = it.name + " - " + messages["player.noMetaData"]

                    //Increase count of the station
                    StationsApi.service
                            .click(it.stationuuid)
                            .compose(applySchedulers())
                            .onErrorResumeNext {
                                //We do not care if this response fails
                                Single.just(ClickResponse(false,
                                        "Error in ClickResponse"))
                            }
                }
                .subscribe({
                    logger.debug { "Click: $it" }
                }, {
                    logger.error(it) { "Error with changing station..." }
                })

        playerTypeProperty.onChange {
            it?.let {
                playerStateProperty.value = PlayerState.Stopped
                MediaPlayerWrapper.init(it)

                if (it == PlayerType.Custom) {
                    fire(NotificationPaneEvent(messages["player.ffmpeg.info"]))
                }
            }
        }

        //Set volume for current player
        //Save the ViewModel after setting new value
        volumeProperty.onChange {
            MediaPlayerWrapper.changeVolume(it)
        }

        playerStateProperty.onChange {
            if (it == PlayerState.Playing) {
                //Ignore stations with empty stream URL
                stationProperty.value.url_resolved?.let { url ->
                    with(MediaPlayerWrapper) {
                        if (isInitialized) {
                            changeVolume(volumeProperty.value)
                            play(url)
                        } else {
                            //Error while initializing player
                            fire(NotificationPaneEvent(messages["player.initError"]))
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
        if (playerStateProperty.value == PlayerState.Playing) {
            playerStateProperty.value = PlayerState.Stopped
        } else {
            playerStateProperty.value = PlayerState.Playing
        }
    }

    override fun onCommit() {
        saveProperties(mapOf(
                Properties.PLAYER to playerTypeProperty.value,
                Properties.PLAYER_ANIMATE to animateProperty.value,
                Properties.VOLUME to volumeProperty.value
        ))
    }
}

